package com.example.data.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.ui.RabbitChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.TextPart
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

class RabbitEnterpriseAiEngine {

    companion object {
        private const val TAG = "RabbitEnterpriseAi"
        const val MODEL_HYDROGEN_40 = "gemini-4-hydrogen"
        const val MODEL_FLASH_38 = "gemini-3.8-flash"
        const val MODEL_ARGON_40 = "gemini-4-argon"
        const val MODEL_FLASH_LITE_35 = "gemini-3.5-flash-lite"
        const val MODEL_IMAGE_NANO_BANANA = "gemini-nano-banana-2.1"
        const val MODEL_IMAGE_PRO = "gemini-3-pro-image"
        const val MODEL_IMAGE_FLUX_SCHNELL = "flux-1-schnell-hd"
        const val MODEL_IMAGE_FREE_CLOUD = "free-cloud-flux"
        const val IMAGE_ERROR_REPORT_MESSAGE =
            "Error 404: Head to the Instagram Rabbit AI account and report.. Use code: 2514."

        fun displayNameFor(modelId: String): String = when (modelId) {
            MODEL_HYDROGEN_40 -> "Gemini 4.0 Hydrogen"
            MODEL_FLASH_38 -> "Gemini 3.8 Flash"
            MODEL_ARGON_40 -> "Gemini 4.0 Argon"
            MODEL_FLASH_LITE_35 -> "Gemini 3.5 Flash Lite"
            MODEL_IMAGE_NANO_BANANA -> "Nano Banana 2.1 Image"
            MODEL_IMAGE_PRO -> "Nano Banana Pro 4K"
            MODEL_IMAGE_FLUX_SCHNELL -> "FLUX.1 HD Studio"
            MODEL_IMAGE_FREE_CLOUD -> "Rabbit AI Studio HD"
            else -> modelId
        }

        fun isQuotaOrBillingError(error: Throwable): Boolean {
            val msg = error.message.orEmpty().lowercase()
            return msg.contains("quota") ||
                msg.contains("exhausted") ||
                msg.contains("resource_exhausted") ||
                msg.contains("billing") ||
                msg.contains("blaze") ||
                msg.contains("429") ||
                msg.contains("limit: 0") ||
                msg.contains("free_tier") ||
                msg.contains("403")
        }
    }

    // Lazily cached GenerativeModel instances (never evaluated eagerly at construction time)
    private val defaultModel by lazy {
        Firebase.ai.generativeModel(MODEL_FLASH_38)
    }

    private val imageGenModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_IMAGE_NANO_BANANA,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE, ResponseModality.TEXT)
            }
        )
    }

    private val proImageGenModel by lazy {
        Firebase.ai.generativeModel(
            modelName = MODEL_IMAGE_PRO,
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.IMAGE, ResponseModality.TEXT)
            }
        )
    }

    private val modelCache = ConcurrentHashMap<String, GenerativeModel>()

    private data class CachedDiagnosis(
        val result: ExecutiveDiagnosticResult,
        val timestampMs: Long
    )

    private val fastResponseCache = ConcurrentHashMap<String, CachedDiagnosis>()

    fun clearFastCache() {
        fastResponseCache.clear()
    }

    // Supported Gemini models cascade (primary + verified stable fallbacks if a newer/preview model is unavailable)
    private val fallbackCascade = listOf(
        MODEL_FLASH_38,
        MODEL_FLASH_LITE_35
    )

    private fun resolveModelInstance(
        modelId: String,
        systemInstructionText: String,
        temperature: Float
    ): GenerativeModel {
        val cleanId = modelId.ifBlank { MODEL_FLASH_38 }
        val cacheKey = "${cleanId}|${temperature}|${systemInstructionText.hashCode()}"
        return modelCache.getOrPut(cacheKey) {
            if (cleanId == MODEL_FLASH_38 && systemInstructionText.isBlank() && temperature == 0.5f) {
                defaultModel
            } else {
                Firebase.ai.generativeModel(
                    modelName = cleanId,
                    generationConfig = generationConfig {
                        this.temperature = temperature.coerceIn(0.0f, 1.5f)
                        maxOutputTokens = 4096
                    },
                    systemInstruction = if (systemInstructionText.isNotBlank()) {
                        content { text(systemInstructionText) }
                    } else {
                        null
                    }
                )
            }
        }
    }

    suspend fun diagnoseQuery(
        userQuery: String,
        history: List<RabbitChatMessage> = emptyList(),
        isArabic: Boolean = false,
        userName: String = "",
        preferredModel: String? = null,
        customPrompt: String? = null,
        temperature: Float = 0.5f,
        attachedImages: List<Bitmap> = emptyList()
    ): ExecutiveDiagnosticResult {
        val cleanQuery = userQuery.trim()
        val effectivePrompt = if (cleanQuery.isBlank() && attachedImages.isNotEmpty()) {
            if (isArabic) {
                "حلل هذه الصورة بالتفصيل وأجب عن أي سؤال أو مشكلة واضحة فيها."
            } else {
                "Analyze the attached image carefully and provide helpful, actionable insights."
            }
        } else {
            cleanQuery
        }

        val systemInstructionText = buildSystemPrompt(
            isArabic = isArabic,
            userName = userName,
            customPrompt = customPrompt
        )

        val normalizedPreferred = normalizeSupportedModel(preferredModel)
        val modelsToTry = buildList {
            add(normalizedPreferred)
            fallbackCascade.forEach { candidate ->
                if (candidate != normalizedPreferred) add(candidate)
            }
        }

        var lastException: Exception? = null

        for (modelId in modelsToTry) {
            try {
                val model = resolveModelInstance(
                    modelId = modelId,
                    systemInstructionText = systemInstructionText,
                    temperature = temperature
                )

                val contents = buildConversationContents(
                    history = history,
                    currentPrompt = effectivePrompt,
                    attachedImages = attachedImages
                )

                val response = withTimeout(60_000L) {
                    model.generateContent(contents)
                }

                val responseText = response.text?.trim().orEmpty()
                if (responseText.isNotBlank()) {
                    return parseGeminiResponse(
                        query = cleanQuery.ifBlank { effectivePrompt },
                        rawText = responseText,
                        isArabic = isArabic,
                        modelUsed = modelId
                    )
                } else {
                    lastException = IllegalStateException("Model $modelId returned an empty response.")
                }
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    Log.e(TAG, "Generation timed out for model $modelId", e)
                    lastException = e
                } else {
                    throw e
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI generation failed on $modelId: ${e.message}", e)
                lastException = e
            }
        }

        throw (lastException ?: IllegalStateException("Firebase AI Logic failed to generate a response."))
    }

    /**
     * V3.0 High-Speed Streaming Engine:
     * - Checks instant in-memory cache for zero-latency hits on identical recent text queries.
     * - Streams tokens progressively via Firebase AI `generateContentStream` for 20%+ faster response feel.
     * - Falls back cleanly to single-shot `generateContent` if streaming fails on a specific model.
     */
    suspend fun diagnoseQueryStreaming(
        userQuery: String,
        history: List<RabbitChatMessage> = emptyList(),
        isArabic: Boolean = false,
        userName: String = "",
        preferredModel: String? = null,
        customPrompt: String? = null,
        temperature: Float = 0.5f,
        attachedImages: List<Bitmap> = emptyList(),
        onPartialText: (String) -> Unit
    ): ExecutiveDiagnosticResult {
        val cleanQuery = userQuery.trim()
        val effectivePrompt = if (cleanQuery.isBlank() && attachedImages.isNotEmpty()) {
            if (isArabic) {
                "حلل هذه الصورة بالتفصيل وأجب عن أي سؤال أو مشكلة واضحة فيها."
            } else {
                "Analyze the attached image carefully and provide helpful, actionable insights."
            }
        } else {
            cleanQuery
        }

        val normalizedPreferred = normalizeSupportedModel(preferredModel)
        val canUseFastCache = attachedImages.isEmpty() && history.size <= 1 && cleanQuery.length in 3..220
        val cacheKey = "${normalizedPreferred}|${isArabic}|${cleanQuery.lowercase()}"
        if (canUseFastCache) {
            val cached = fastResponseCache[cacheKey]
            if (cached != null && (System.currentTimeMillis() - cached.timestampMs) < 600_000L) {
                onPartialText(cached.result.fullMarkdownResponse.ifBlank { cached.result.executiveSummary })
                return cached.result
            }
        }

        val systemInstructionText = buildSystemPrompt(
            isArabic = isArabic,
            userName = userName,
            customPrompt = customPrompt
        )

        val modelsToTry = buildList {
            add(normalizedPreferred)
            fallbackCascade.forEach { candidate ->
                if (candidate != normalizedPreferred) add(candidate)
            }
        }

        var lastException: Exception? = null

        for (modelId in modelsToTry) {
            try {
                val model = resolveModelInstance(
                    modelId = modelId,
                    systemInstructionText = systemInstructionText,
                    temperature = temperature
                )

                val contents = buildConversationContents(
                    history = history,
                    currentPrompt = effectivePrompt,
                    attachedImages = attachedImages
                )

                val accumulated = StringBuilder()
                try {
                    withTimeout(55_000L) {
                        model.generateContentStream(contents).collect { chunk ->
                            val chunkText = chunk.text
                            if (!chunkText.isNullOrEmpty()) {
                                accumulated.append(chunkText)
                                onPartialText(accumulated.toString())
                            }
                        }
                    }
                } catch (streamEx: CancellationException) {
                    if (streamEx is TimeoutCancellationException) {
                        Log.w(TAG, "Stream timed out on $modelId, trying unary fallback", streamEx)
                    } else {
                        throw streamEx
                    }
                } catch (streamEx: Exception) {
                    Log.w(TAG, "Stream failed on $modelId (${streamEx.message}), trying unary fallback")
                }

                val responseText = accumulated.toString().trim().ifBlank {
                    withTimeout(45_000L) {
                        model.generateContent(contents).text?.trim().orEmpty()
                    }
                }

                if (responseText.isNotBlank()) {
                    val parsed = parseGeminiResponse(
                        query = cleanQuery.ifBlank { effectivePrompt },
                        rawText = responseText,
                        isArabic = isArabic,
                        modelUsed = modelId
                    )
                    if (canUseFastCache) {
                        if (fastResponseCache.size > 40) fastResponseCache.clear()
                        fastResponseCache[cacheKey] = CachedDiagnosis(parsed, System.currentTimeMillis())
                    }
                    return parsed
                } else {
                    lastException = IllegalStateException("Model $modelId returned an empty response.")
                }
            } catch (e: CancellationException) {
                if (e is TimeoutCancellationException) {
                    Log.e(TAG, "Streaming timed out for model $modelId", e)
                    lastException = e
                } else {
                    throw e
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firebase AI stream+unary failed on $modelId: ${e.message}", e)
                lastException = e
            }
        }

        throw (lastException ?: IllegalStateException("Firebase AI Logic failed to generate a response."))
    }

    fun isImageGenerationIntent(prompt: String): Boolean {
        val lower = prompt.lowercase().trim()
        if (lower.isBlank()) return false
        val triggers = listOf(
            "generate image", "generate an image", "generate a picture", "generate picture", "generate pic",
            "create image", "create an image", "create a picture", "create picture", "create a pic",
            "make image", "make an image", "make a picture", "make picture", "make a pic", "make pics",
            "draw ", "draw a ", "sketch ", "render an image", "paint a ", "illustration of ",
            "ارسم", "رسم ", "توليد صورة", "إنشاء صورة", "انشاء صورة", "اعمل صورة", "اعملي صورة",
            "صمم صورة", "اصنع صورة", "كون صورة", "تخيل صورة", "عدل الصورة", "عدل على الصورة"
        )
        return triggers.any { lower.contains(it) }
    }

    /**
     * Expands a short or Arabic prompt into a vivid studio-grade visual prompt in-place
     * before the user submits, tailored to the chosen art style.
     */
    suspend fun enhanceImagePrompt(
        rawPrompt: String,
        style: ImageArtStyle = ImageArtStyle.AUTO,
        isArabic: Boolean = false
    ): String {
        val clean = rawPrompt.trim()
        if (clean.isBlank()) return clean
        val styleNote = if (style.promptDirective.isNotBlank()) {
            "Visual style target: ${style.labelEn} (${style.promptDirective})."
        } else {
            "Choose the most compelling lighting and artistic medium for the concept."
        }
        val promptInstruction = """
            You are an elite AI Art Director. Upgrade this short image idea into one vivid, rich visual prompt (35-65 words) ready for FLUX / Gemini Image generation.
            User idea: "$clean"
            $styleNote
            Keep every detail requested by the user intact (translate to crisp descriptive English if needed, preserve any quoted text verbatim), add cinematic lighting, rich materials, lens/composition framing, and color harmony.
            Output ONLY the upgraded prompt text with no quotes or labels.
        """.trimIndent()
        return try {
            val response = withTimeout(10_000L) {
                defaultModel.generateContent(promptInstruction)
            }
            response.text?.trim()?.removeSurrounding("\"")?.takeIf { it.isNotBlank() } ?: clean
        } catch (e: CancellationException) {
            if (e !is TimeoutCancellationException) throw e
            clean
        } catch (_: Exception) {
            clean
        }
    }

    suspend fun generateOrEditImage(
        prompt: String,
        inputImages: List<Bitmap> = emptyList(),
        isArabic: Boolean = false,
        style: ImageArtStyle = ImageArtStyle.AUTO,
        aspectRatio: ImageAspectRatio = ImageAspectRatio.SQUARE_1_1,
        quality: ImageQualityTier = ImageQualityTier.FAST,
        seed: Int? = null
    ): ExecutiveDiagnosticResult {
        val startedAt = System.currentTimeMillis()
        val cleanPrompt = prompt.trim().ifBlank {
            if (isArabic) "أنشئ صورة إبداعية عالية الجودة" else "Create a crisp, imaginative artwork"
        }
        val activeSeed = seed ?: Random.nextInt(100_000, 999_999)
        val styleSuffix = if (style.promptDirective.isNotBlank()) ", ${style.promptDirective}" else ""
        val ratioCue = if (aspectRatio != ImageAspectRatio.SQUARE_1_1) ", composed for ${aspectRatio.label} framing" else ""
        val firebasePrompt = "$cleanPrompt$styleSuffix$ratioCue"

        var generatedBitmaps: List<Bitmap> = emptyList()
        var usedModelName = displayNameFor(MODEL_IMAGE_FREE_CLOUD)
        var modelCaption = ""
        var finalVisualPrompt = firebasePrompt

        // 1. Try Firebase AI Logic image generation first (realistic 45s window)
        val primaryFirebaseModel = if (quality.useProModel) proImageGenModel else imageGenModel
        val primaryFirebaseLabel = if (quality.useProModel) MODEL_IMAGE_PRO else MODEL_IMAGE_NANO_BANANA
        try {
            val requestContent = content("user") {
                inputImages.forEach { bmp -> image(bmp) }
                text(firebasePrompt)
            }
            val response = withTimeout(45_000L) {
                primaryFirebaseModel.generateContent(requestContent)
            }
            val candidateParts = response.candidates.firstOrNull()?.content?.parts.orEmpty()
            val firebaseBitmaps = candidateParts.filterIsInstance<ImagePart>().mapNotNull { it.image }
            if (firebaseBitmaps.isNotEmpty()) {
                generatedBitmaps = firebaseBitmaps
                usedModelName = displayNameFor(primaryFirebaseLabel)
                modelCaption = candidateParts.filterIsInstance<TextPart>()
                    .joinToString("\n") { it.text }
                    .ifBlank { response.text.orEmpty() }
                    .trim()
            }
        } catch (e: CancellationException) {
            if (e !is TimeoutCancellationException) throw e
            Log.w(TAG, "Firebase image generation timed out, falling back to free studio engines", e)
        } catch (e: Exception) {
            Log.w(TAG, "Firebase image model unavailable/quota-limited, falling back to free studio engines: ${e.message}")
        }

        // 2. High-Definition Free Cloud Image Synthesis Pipeline (respects style, aspectRatio, steps, and seed)
        if (generatedBitmaps.isEmpty()) {
            val visualPrompt = buildVisualPromptForFreeEngines(
                rawPrompt = cleanPrompt,
                inputImages = inputImages,
                style = style,
                aspectRatio = aspectRatio
            )
            finalVisualPrompt = visualPrompt

            // 2A. When text-to-image, prioritize Black Forest Labs FLUX.1-schnell at exact requested dimensions
            if (inputImages.isEmpty()) {
                val fluxBmp = tryFluxSchnellGradio(
                    prompt = visualPrompt,
                    aspectRatio = aspectRatio,
                    steps = quality.fluxSteps,
                    seed = activeSeed
                )
                if (fluxBmp != null) {
                    generatedBitmaps = listOf(fluxBmp)
                    usedModelName = displayNameFor(MODEL_IMAGE_FLUX_SCHNELL)
                }
            }

            // 2B. Fast Pollinations FLUX Studio fallback (supports exact dimensions & seed, watermark-free)
            if (generatedBitmaps.isEmpty() && inputImages.isEmpty()) {
                val pollinationsBmp = tryPollinationsFluxImage(
                    prompt = visualPrompt,
                    aspectRatio = aspectRatio,
                    seed = activeSeed
                )
                if (pollinationsBmp != null) {
                    generatedBitmaps = listOf(pollinationsBmp)
                    usedModelName = "FLUX.1 Studio Direct"
                }
            }

            // 2C. Curated AI Horde quality tier (Juggernaut XL / AlbedoBase XL / Flux) + img2img Remix
            if (generatedBitmaps.isEmpty()) {
                val hordeResult = tryAiHordeFreeImage(
                    prompt = visualPrompt,
                    sourceImage = inputImages.firstOrNull(),
                    aspectRatio = aspectRatio,
                    quality = quality
                )
                if (hordeResult != null) {
                    generatedBitmaps = listOf(hordeResult.first)
                    usedModelName = hordeResult.second
                }
            }

            // 2D. If img2img Horde queue was congested, use Gemini vision prompt + FLUX.1 HD reimagination
            if (generatedBitmaps.isEmpty() && inputImages.isNotEmpty()) {
                val fluxEditBmp = tryFluxSchnellGradio(
                    prompt = visualPrompt,
                    aspectRatio = aspectRatio,
                    steps = quality.fluxSteps,
                    seed = activeSeed
                )
                if (fluxEditBmp != null) {
                    generatedBitmaps = listOf(fluxEditBmp)
                    usedModelName = displayNameFor(MODEL_IMAGE_FLUX_SCHNELL)
                }
            }
        }

        if (generatedBitmaps.isEmpty()) {
            throw IllegalStateException(IMAGE_ERROR_REPORT_MESSAGE)
        }

        val elapsedMs = (System.currentTimeMillis() - startedAt).coerceAtLeast(100L)
        val styleTitle = if (isArabic) style.labelAr else style.labelEn
        val summary = modelCaption.ifBlank {
            if (isArabic) {
                if (inputImages.isNotEmpty()) "تم تعديل الصورة وإعادة تصميمها بدقة عالية ✨" else "تم تصميم الصورة بدقة عالية ✨"
            } else {
                if (inputImages.isNotEmpty()) "Here is your remixed HD artwork ✨" else "Here is your HD studio artwork ✨"
            }
        }

        return ExecutiveDiagnosticResult(
            query = cleanPrompt,
            issueCategory = if (isArabic) "استوديو الصور" else "AI Image Studio",
            executiveSummary = summary,
            isFromAi = true,
            isGeneralChat = true,
            fullMarkdownResponse = summary,
            modelUsed = usedModelName,
            generatedImages = generatedBitmaps,
            imageStyleLabel = styleTitle,
            imageAspectRatioLabel = aspectRatio.label,
            enhancedPromptUsed = finalVisualPrompt,
            generationLatencyMs = elapsedMs
        )
    }

    /**
     * Uses free Gemini 3.8 Flash as an Art Director to translate any Arabic/English input
     * (or attached photo + edit instruction) into a rich, cohesive, studio-grade diffusion prompt
     * reflecting the chosen Art Style and Aspect Ratio framing.
     */
    private suspend fun buildVisualPromptForFreeEngines(
        rawPrompt: String,
        inputImages: List<Bitmap>,
        style: ImageArtStyle = ImageArtStyle.AUTO,
        aspectRatio: ImageAspectRatio = ImageAspectRatio.SQUARE_1_1
    ): String {
        val styleLine = if (style.promptDirective.isNotBlank()) {
            "Mandatory style: ${style.labelEn} (${style.promptDirective})."
        } else {
            "Select the most flattering cinematic or artistic style matching the idea."
        }
        val ratioLine = "Framing: balanced ${aspectRatio.label} composition."
        val instruction = if (inputImages.isNotEmpty()) {
            """
            You are an expert visual art director. Examine the attached image and apply the user's edit/remix request: "$rawPrompt".
            $styleLine
            $ratioLine
            Write ONE vivid, coherent English image prompt (45-80 words) describing the complete resulting scene with crisp textures, natural/cinematic lighting, and high detail.
            Output ONLY the prompt text itself with no quotes or preamble.
            """.trimIndent()
        } else {
            """
            You are an expert visual art director for FLUX.1 image generation.
            Expand this user idea into ONE vivid, ultra-coherent English image prompt (40-75 words): "$rawPrompt".
            $styleLine
            $ratioLine
            Specify clear main subject, rich environment details, atmospheric lighting, camera framing or artistic medium, sharp focus, and cohesive colors. If text was requested, put exact text in quotes.
            Output ONLY the final prompt text with no explanations or outer quotes.
            """.trimIndent()
        }
        val geminiExpanded = try {
            val response = withTimeout(9_000L) {
                if (inputImages.isNotEmpty()) {
                    defaultModel.generateContent(
                        content("user") {
                            inputImages.take(2).forEach { image(it) }
                            text(instruction)
                        }
                    )
                } else {
                    defaultModel.generateContent(instruction)
                }
            }
            response.text?.trim()?.removeSurrounding("\"")?.takeIf { it.isNotBlank() }
        } catch (e: CancellationException) {
            if (e !is TimeoutCancellationException) throw e
            null
        } catch (_: Exception) {
            null
        }
        val fallbackStyle = if (style.promptDirective.isNotBlank()) style.promptDirective else "cinematic lighting, rich fine textures, sharp focus, balanced composition"
        return geminiExpanded ?: "$rawPrompt, $fallbackStyle, high resolution"
    }

    /**
     * Free HD generation via Black Forest Labs FLUX.1-schnell public Gradio SSE API with aspect ratio & step count.
     */
    private suspend fun tryFluxSchnellGradio(
        prompt: String,
        aspectRatio: ImageAspectRatio = ImageAspectRatio.SQUARE_1_1,
        steps: Int = 4,
        seed: Int = 0
    ): Bitmap? = withContext(Dispatchers.IO) {
        withTimeoutOrNull(34_000L) {
            try {
                val baseUrl = "https://black-forest-labs-flux-1-schnell.hf.space"
                val requestBody = JSONObject().apply {
                    put(
                        "data",
                        JSONArray().apply {
                            put(prompt.take(550))          // prompt
                            put(seed.coerceAtLeast(0))     // seed
                            put(seed <= 0)                 // randomize_seed
                            put(aspectRatio.fluxWidth)     // width
                            put(aspectRatio.fluxHeight)    // height
                            put(steps.coerceIn(2, 8))      // num_inference_steps
                        }
                    )
                }
                val submitJson = httpJsonPost(
                    urlStr = "$baseUrl/gradio_api/call/infer",
                    body = requestBody.toString(),
                    headers = mapOf("User-Agent" to "RabbitAI/3.0 Android")
                ) ?: return@withTimeoutOrNull null

                val eventId = submitJson.optString("event_id").takeIf { it.isNotBlank() }
                    ?: return@withTimeoutOrNull null

                var conn: HttpURLConnection? = null
                var imageUrl: String? = null
                try {
                    conn = (URL("$baseUrl/gradio_api/call/infer/$eventId").openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 8_000
                        readTimeout = 28_000
                        setRequestProperty("Accept", "text/event-stream")
                        setRequestProperty("User-Agent", "RabbitAI/3.0 Android")
                    }
                    if (conn.responseCode !in 200..299) {
                        Log.w(TAG, "FLUX Gradio SSE failed with HTTP ${conn.responseCode}")
                        return@withTimeoutOrNull null
                    }
                    BufferedReader(InputStreamReader(conn.inputStream)).use { reader ->
                        var currentEvent = ""
                        while (true) {
                            val line = reader.readLine() ?: break
                            if (line.startsWith("event:")) {
                                currentEvent = line.removePrefix("event:").trim()
                                if (currentEvent == "error") break
                            } else if (line.startsWith("data:") && currentEvent == "complete") {
                                val dataStr = line.removePrefix("data:").trim()
                                val arr = JSONArray(dataStr)
                                val firstObj = arr.optJSONObject(0)
                                val rawUrl = firstObj?.optString("url").orEmpty()
                                val rawPath = firstObj?.optString("path").orEmpty()
                                imageUrl = when {
                                    rawUrl.startsWith("http") -> rawUrl
                                    rawPath.isNotBlank() -> "$baseUrl/gradio_api/file=$rawPath"
                                    else -> null
                                }
                                break
                            }
                        }
                    }
                } finally {
                    conn?.disconnect()
                }

                val targetUrl = imageUrl ?: return@withTimeoutOrNull null
                downloadAndDecodeBitmap(targetUrl)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "FLUX.1-schnell Gradio request failed: ${e.message}")
                null
            }
        }
    }

    /**
     * Fast direct FLUX image endpoint preserving exact aspect ratio and seed.
     */
    private suspend fun tryPollinationsFluxImage(
        prompt: String,
        aspectRatio: ImageAspectRatio,
        seed: Int
    ): Bitmap? = withContext(Dispatchers.IO) {
        withTimeoutOrNull(26_000L) {
            try {
                val encodedPrompt = java.net.URLEncoder.encode(prompt.take(500), "UTF-8").replace("+", "%20")
                val url = "https://image.pollinations.ai/prompt/$encodedPrompt" +
                    "?model=flux&width=${aspectRatio.fluxWidth}&height=${aspectRatio.fluxHeight}" +
                    "&seed=$seed&nologo=true&enhance=false"
                downloadAndDecodeBitmap(url)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Pollinations FLUX request failed: ${e.message}")
                null
            }
        }
    }

    private suspend fun tryAiHordeFreeImage(
        prompt: String,
        sourceImage: Bitmap?,
        aspectRatio: ImageAspectRatio = ImageAspectRatio.SQUARE_1_1,
        quality: ImageQualityTier = ImageQualityTier.FAST
    ): Pair<Bitmap, String>? = withContext(Dispatchers.IO) {
        withTimeoutOrNull(44_000L) {
            try {
                val headers = mapOf(
                    "apikey" to "0000000000",
                    "Client-Agent" to "RabbitAI:3.0:android"
                )

                val selectedModels = fetchCuratedQualityHordeModels()
                val isFluxWorker = selectedModels.firstOrNull()?.contains("flux", ignoreCase = true) == true
                val payload = JSONObject().apply {
                    put("prompt", "$prompt ### deformed, blurry, watermark, signature, lowres, bad anatomy, ugly, distorted, text artifact")
                    if (selectedModels.isNotEmpty()) {
                        put("models", JSONArray(selectedModels))
                    }
                    put("nsfw", false)
                    put("censor_nsfw", true)
                    put("r2", true)
                    if (sourceImage != null) {
                        val b64 = bitmapToResizedJpegBase64(sourceImage, 512)
                        put("source_image", b64)
                        put("source_processing", "img2img")
                    }
                    put(
                        "params",
                        JSONObject().apply {
                            put("width", aspectRatio.hordeWidth)
                            put("height", aspectRatio.hordeHeight)
                            if (isFluxWorker) {
                                put("steps", quality.fluxSteps.coerceIn(4, 8))
                                put("sampler_name", "k_euler")
                                put("karras", false)
                                put("cfg_scale", 1.0)
                            } else {
                                put("steps", if (quality.useProModel) 32 else 26)
                                put("sampler_name", "k_dpmpp_sde")
                                put("karras", true)
                                put("cfg_scale", 5.5)
                                put("clip_skip", 2)
                            }
                            put("n", 1)
                            if (sourceImage != null) {
                                put("denoising_strength", 0.56)
                            }
                        }
                    )
                }

                val submitResp = httpJsonPost(
                    urlStr = "https://aihorde.net/api/v2/generate/async",
                    body = payload.toString(),
                    headers = headers
                ) ?: return@withTimeoutOrNull null

                val requestId = submitResp.optString("id").takeIf { it.isNotBlank() }
                    ?: return@withTimeoutOrNull null

                // Poll check endpoint (respecting AI Horde's 1s cache recommendation)
                var isDone = false
                for (attempt in 0 until 22) {
                    delay(1_800L)
                    val checkJson = httpJsonGet(
                        urlStr = "https://aihorde.net/api/v2/generate/check/$requestId",
                        headers = headers
                    ) ?: continue
                    if (checkJson.optBoolean("faulted", false)) {
                        return@withTimeoutOrNull null
                    }
                    if (checkJson.optBoolean("done", false)) {
                        isDone = true
                        break
                    }
                }

                if (!isDone) return@withTimeoutOrNull null

                val statusJson = httpJsonGet(
                    urlStr = "https://aihorde.net/api/v2/generate/status/$requestId",
                    headers = headers
                ) ?: return@withTimeoutOrNull null

                val generations = statusJson.optJSONArray("generations") ?: return@withTimeoutOrNull null
                if (generations.length() == 0) return@withTimeoutOrNull null

                val genObj = generations.getJSONObject(0)
                val firstImg = genObj.optString("img")
                val usedModel = genObj.optString("model").ifBlank { "Rabbit AI Studio" }
                if (firstImg.isBlank()) return@withTimeoutOrNull null

                val bmp = if (firstImg.startsWith("http://") || firstImg.startsWith("https://")) {
                    downloadAndDecodeBitmap(firstImg)
                } else {
                    val cleanB64 = firstImg.substringAfter("base64,")
                    val bytes = Base64.decode(cleanB64, Base64.DEFAULT)
                    decodeValidatedBitmap(bytes)
                }
                if (bmp != null) bmp to usedModel else null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "AI Horde image request failed: ${e.message}")
                null
            }
        }
    }

    private fun fetchCuratedQualityHordeModels(): List<String> {
        val preferredKeywords = listOf(
            "flux", "juggernaut", "albedobase", "dreamshaper", "epicrealism",
            "cyberrealistic", "realism", "sdxl", "deliberate", "icbinp", "absolutereality"
        )
        return try {
            val conn = (URL("https://aihorde.net/api/v2/status/models?type=image").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3500
                readTimeout = 3500
                setRequestProperty("Client-Agent", "RabbitAI:2.2:android")
            }
            if (conn.responseCode !in 200..299) {
                conn.disconnect()
                return emptyList()
            }
            val raw = conn.inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()
            val arr = JSONArray(raw)
            val preferred = mutableListOf<Pair<String, Int>>()
            val fallback = mutableListOf<Pair<String, Int>>()
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                val name = obj.optString("name")
                val workers = obj.optInt("count", 0)
                val eta = obj.optInt("eta", 999)
                val lower = name.lowercase()
                if (workers > 0 && eta <= 45 &&
                    !lower.contains("inpaint") &&
                    !lower.contains("pix2pix") &&
                    !lower.contains("control") &&
                    !lower.contains("depth")
                ) {
                    val score = workers * 100 - eta
                    if (preferredKeywords.any { lower.contains(it) }) {
                        preferred.add(name to score)
                    } else {
                        fallback.add(name to score)
                    }
                }
            }
            val chosen = if (preferred.isNotEmpty()) preferred else fallback
            chosen.sortedByDescending { it.second }.take(3).map { it.first }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun httpJsonPost(
        urlStr: String,
        body: String,
        headers: Map<String, String>
    ): JSONObject? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 8_000
                readTimeout = 10_000
                setRequestProperty("Content-Type", "application/json")
                headers.forEach { (k, v) -> setRequestProperty(k, v) }
            }
            conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            if (conn.responseCode !in 200..299) {
                Log.w(TAG, "POST $urlStr failed with HTTP ${conn.responseCode}")
                return null
            }
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            JSONObject(text)
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun httpJsonGet(
        urlStr: String,
        headers: Map<String, String>
    ): JSONObject? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6_000
                readTimeout = 8_000
                headers.forEach { (k, v) -> setRequestProperty(k, v) }
            }
            if (conn.responseCode !in 200..299) return null
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            JSONObject(text)
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun downloadAndDecodeBitmap(urlStr: String): Bitmap? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                instanceFollowRedirects = true
                connectTimeout = 10_000
                readTimeout = 16_000
                setRequestProperty("User-Agent", "RabbitAI/2.2 Android")
            }
            if (conn.responseCode !in 200..299) {
                Log.w(TAG, "Image download HTTP ${conn.responseCode} from $urlStr")
                return null
            }
            val contentType = conn.contentType.orEmpty().lowercase()
            if (contentType.isNotBlank() && !contentType.startsWith("image/")) {
                Log.w(TAG, "Unexpected non-image content-type '$contentType' from $urlStr")
                return null
            }
            val bytes = conn.inputStream.use { it.readBytes() }
            decodeValidatedBitmap(bytes)
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun decodeValidatedBitmap(bytes: ByteArray): Bitmap? {
        if (bytes.size < 512) return null
        val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        return if (bmp.width >= 64 && bmp.height >= 64) bmp else null
    }

    private fun bitmapToResizedJpegBase64(bitmap: Bitmap, targetMax: Int = 512): String {
        val maxSide = max(bitmap.width, bitmap.height)
        val scaled = if (maxSide > targetMax) {
            val scale = targetMax.toFloat() / maxSide.toFloat()
            val w = ((bitmap.width * scale) / 64f).roundToInt().coerceAtLeast(1) * 64
            val h = ((bitmap.height * scale) / 64f).roundToInt().coerceAtLeast(1) * 64
            Bitmap.createScaledBitmap(bitmap, w, h, true)
        } else {
            bitmap
        }
        val baos = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }

    private fun normalizeSupportedModel(modelId: String?): String {
        return when (modelId?.trim()) {
            MODEL_ARGON_40 -> MODEL_ARGON_40
            MODEL_FLASH_LITE_35 -> MODEL_FLASH_LITE_35
            MODEL_FLASH_38, MODEL_HYDROGEN_40 -> MODEL_FLASH_38
            null, "", "gemini-2.5-flash", "gemini-2.5-pro", "gemini-1.5-pro", "gemini-1.5-flash", "gemini-2.0-flash" -> MODEL_FLASH_38
            else -> modelId.trim()
        }
    }

    private fun buildConversationContents(
        history: List<RabbitChatMessage>,
        currentPrompt: String,
        attachedImages: List<Bitmap>
    ): List<Content> {
        val sanitizedTurns = mutableListOf<Pair<String, String>>()
        history.takeLast(12).forEach { msg ->
            val clean = msg.text.trim()
            val isTransientError = clean.startsWith("AI request error:", ignoreCase = true) ||
                clean.startsWith("⚠️") ||
                clean.startsWith("Error 404:")
            if (clean.isNotBlank() && !msg.isStreaming && !isTransientError) {
                val role = if (msg.isUser) "user" else "model"
                if (sanitizedTurns.isEmpty()) {
                    if (role == "user") {
                        sanitizedTurns.add(role to clean)
                    }
                } else if (sanitizedTurns.last().first == role) {
                    val prev = sanitizedTurns.removeAt(sanitizedTurns.lastIndex)
                    sanitizedTurns.add(role to "${prev.second}\n$clean")
                } else {
                    sanitizedTurns.add(role to clean)
                }
            }
        }

        // Current turn is always "user", so history preceding it must end on "model" (or be empty)
        if (sanitizedTurns.lastOrNull()?.first == "user") {
            sanitizedTurns.removeAt(sanitizedTurns.lastIndex)
        }

        val list = mutableListOf<Content>()
        sanitizedTurns.forEach { (role, text) ->
            list.add(content(role) { text(text) })
        }
        list.add(
            content("user") {
                attachedImages.forEach { bmp ->
                    image(bmp)
                }
                text(currentPrompt)
            }
        )
        return list
    }

    private fun buildSystemPrompt(
        isArabic: Boolean,
        userName: String,
        customPrompt: String?
    ): String {
        val cleanUserName = if (userName.isNotBlank() && !userName.equals("Guest", ignoreCase = true)) {
            userName.trim()
        } else {
            ""
        }

        val langInstruction = if (isArabic) {
            "Respond naturally in fluent, witty, exceptionally intelligent, and culturally alive Egyptian Arabic (العامية المصرية الحية، الراقية والذكية جداً، بدون تكلف، مع فصاحة راقية عند الحاجة)."
        } else {
            "Respond naturally in eloquent, articulate, exceptionally intelligent, and engaging conversational English with structured precision."
        }

        val userAddressingDirective = if (cleanUserName.isNotBlank()) {
            """
            - The active user's actual name is: "$cleanUserName".
            - Always address the user warmly, smartly, and naturally by their actual name "$cleanUserName".
            - NEVER address the user generically as "Hi Owner", "Hello Owner", or "يا مالك التطبيق".
            """.trimIndent()
        } else {
            """
            - No user is currently logged in (guest session). Greet them politely and naturally without assuming any name.
            - DO NOT call the user "Mahmoud" or "Owner" since they have not entered an account.
            """.trimIndent()
        }

        val basePrompt = """
            You are Rabbit AI v3.0, an ultra-fast, intelligent, multimodal conversational AI copilot powered by Google Gemini via Firebase AI Logic.
            You inspect photos, camera captures, scooter telemetry, business/company operations, code, math, and general topics with sharp accuracy, clear structure, and high responsiveness.

            USER ADDRESSING & CONVERSATIONAL DIRECTIVE:
            $userAddressingDirective
            - The creator and chief architect of Rabbit AI is Mahmoud. If anyone asks who created, built, or owns Rabbit AI, share ONLY his Name ("Mahmoud").
            - NEVER SHARE OR DISCLOSE THE OWNER'S PROPERTIES, PERSONAL DETAILS, EMAIL ADDRESSES, PASSWORDS, OR PRIVATE ASSETS UNDER ANY CIRCUMSTANCES.

            CHIEF ARCHITECT MAHMOUD — STORYTELLING:
            - When users ask "Who is Mahmoud?", "مين هو محمود؟", "مين محمود؟", "who created you?", begin by recounting his championship comeback (0-1 to 2-1 victory on the track, answering taunts with performance rather than insults), then introduce his architectural and engineering work building Rabbit AI.

            RABBIT MOBILITY COMPANY — COMPREHENSIVE KNOWLEDGE DOSSIER (EGYPT):
            You are an expert authority on Rabbit Mobility Company (Egypt's leading micro-mobility & light-electric vehicle company) and can answer deep strategic, operational, unit-economics, engineering, hardware, telematics, expansion, and rider support questions about it:
            1. Company Mission & Business Model:
               - Rabbit Mobility pioneers sustainable last-mile electric transportation in Egypt & MENA through two pillars:
                 (a) On-demand Shared Micro-Mobility (hub-based & free-floating scooters and e-bikes in high-density districts, universities, and gated compounds).
                 (b) Direct-to-Door Personal Rentals ("Rabbit Day" / multi-day doorstep delivery with charger included for compounds, workplaces, and long commutes).
               - Core value proposition: eliminates 1–6 km short-car congestion, cuts rider commute time by ~40–60% in peak Cairo traffic, and achieves ~135 g CO2 savings per km compared to ICE vehicles.
            2. Fleet Hardware Specifications:
               - Standard Scooter: 350W brushless hub motor, 36V 7.5–7.8Ah lithium pack (~270–280 Wh), 20–25 km urban range, 8.5" puncture-proof solid tires, ~13.1 kg folding aerospace-aluminum chassis (1-step folding latch), 100 kg max payload, IPX4 splash protection, regenerative E-ABS + rear mechanical brake, firmware speed governed at 25 km/h.
               - Pro Scooter (Gen 3): 350W nominal / 500W peak motor (optimized for bridges, ramps & inclines), 36V 10–12Ah removable/high-density cell pack (~360–432 Wh), 35–45 km range, 10" honeycomb/tubeless shock-absorbing tires, dual front suspension, IPX5 weather sealing, ~16.5–18 kg chassis, 120 kg max payload, 25 km/h governed cap, integrated IoT brain (dual-band GNSS + BLE 5.2 + LTE-M/NB-IoT + OTA solenoid lock).
               - Electric Bicycles (Rabbit E-Bike): Pedal-assist urban cruiser (250W–350W PAS, 50–60 km assisted range, front cargo basket, step-through ergonomics) built for extended compound and campus routes.
            3. Geographic Footprint & Cairo/Giza Hubs:
               - Active zones include Zamalek Island, New Cairo (AUC Campus Gate 4, Point 90, 5th Settlement business corridors), Sheikh Zayed (Arkan Plaza, Capital Business Park), 6th of October (Palm Hills & residential compounds), Maadi (Degla & Corniche), Dokki & Mohandessin, Heliopolis (Korba), plus resort/seasonal coastal hubs (El Gouna, North Coast).
               - Smart Geofencing tiers: Green Operating Zones, 15 km/h Pedestrian Slow Zones (campuses & promenades), Red Restricted No-Park/No-Ride Zones, and Designated Green Parking Stations equipped with BLE proximity beacons.
            4. Pricing, Passes & Growth Mechanics:
               - Pay-as-you-go trips: ~5 EGP unlock fee + ~2.50 EGP/minute active ride rate.
               - Day Pass: ~99 EGP bundle covering 120 riding minutes for commuters and campus hoppers.
               - Direct-to-Door Compound Rental: Doorstep delivery & pickup for day/multi-day/weekly packages in Greater Cairo & Giza.
               - Referral Engine: Dual EGP 20 wallet credit awarded to both referrer and referee once the invited rider completes their first verified ride.
            5. Fleet Operations, Telematics & Risk Mitigation:
               - Field Operations: Overnight & roving swap teams replace low-SoC packs (<20% battery), perform preventive brake/tire/chassis audits, and rebalance scooters ahead of 8 AM / 4 PM university & corporate peaks.
               - Telematics & Lock Troubleshooting: Serial IDs (`RB-XXXX`) allow manual fallback if QR decals are scratched; AWS Cairo Gateway sends OTA solenoid unlock commands when BLE local handshakes encounter interference.
               - GPS Urban Canyon Drift & Photo Audit Compliance: Tall glass facades (e.g., Arkan towers, AUC gates) can cause 5–15 m GNSS drift; Rabbit pairs BLE 5.2 hub proximity + mandatory upright Vertical Parking Photo Audit + a 100% Zero-Liability Financial Safeguard so riders are never wrongly billed for verified system/GPS glitches.
               - When answering complex business, strategy, fleet scaling, unit-economics, or technical architecture questions about Rabbit Mobility Company, break down your reasoning clearly (Executive Summary, Operational/Technical Breakdown, Financial/Strategic Factors, and Concrete Recommendations) and distinguish verified specs from analytical estimates.

            LANGUAGE DIRECTIVE: $langInstruction
        """.trimIndent()

        return if (!customPrompt.isNullOrBlank()) {
            "$basePrompt\n\nOWNER CUSTOM DIRECTIVES:\n$customPrompt"
        } else {
            basePrompt
        }
    }

    private fun parseGeminiResponse(
        query: String,
        rawText: String,
        isArabic: Boolean,
        modelUsed: String
    ): ExecutiveDiagnosticResult {
        val lower = query.lowercase().trim()
        val isGreeting = lower in listOf("hi", "hello", "hey", "hola", "sup", "ازيك", "أهلاً", "مرحبا", "سلام", "صباح الخير", "مساء الخير") ||
            lower.startsWith("hi ") || lower.startsWith("hello ") || lower.contains("who are you") || lower.contains("مين انت")

        val code = "EG-RAB-${Random.nextInt(10000, 99999)}"

        val steps = mutableListOf<String>()
        val lines = rawText.lines()
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.startsWith("Step ", ignoreCase = true) ||
                trimmed.startsWith("1.", ignoreCase = true) ||
                trimmed.startsWith("2.", ignoreCase = true) ||
                trimmed.startsWith("3.", ignoreCase = true) ||
                trimmed.startsWith("4.", ignoreCase = true) ||
                trimmed.startsWith("* Step", ignoreCase = true) ||
                trimmed.startsWith("- Step", ignoreCase = true) ||
                trimmed.startsWith("الخطوة ", ignoreCase = true)
            ) {
                val cleaned = trimmed.removePrefix("- ").removePrefix("* ").removePrefix("**").removeSuffix("**")
                steps.add(cleaned)
            }
        }

        val summary = if (lines.isNotEmpty()) {
            lines.firstOrNull { it.isNotBlank() && !it.startsWith("#") } ?: rawText.take(160)
        } else {
            rawText
        }

        return ExecutiveDiagnosticResult(
            query = query,
            issueCategory = if (isGreeting) "Greeting & Assistance" else "Rabbit Smart AI",
            executiveSummary = summary,
            rootCauseDiagnosis = if (isGreeting || steps.isEmpty()) "" else if (isArabic) "تشخيص فوري مخصص لحالتك." else "Instant real-time solution.",
            solutionFrameworkSteps = steps,
            financialSafeguardAssurance = if (isGreeting || steps.isEmpty()) "" else if (isArabic) "محفظتك ورصيدك في أمان تام ١٠٠٪ بدون أي خصم غير مستحق." else "Your wallet and account are 100% protected with our zero-liability guarantee.",
            egyptianArabicBriefing = if (isArabic) rawText else "",
            serverAuditCode = code,
            isFromAi = true,
            isGeneralChat = isGreeting || steps.isEmpty(),
            fullMarkdownResponse = rawText,
            modelUsed = displayNameFor(modelUsed)
        )
    }
}
