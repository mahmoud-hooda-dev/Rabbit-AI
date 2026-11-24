package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class AiVoiceProfile(
    val id: String,
    val displayName: String,
    val arabicName: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val pitch: Float,
    val speechRate: Float,
    val preferredGender: String // "female", "male", "neutral"
) {
    NOVA(
        id = "nova",
        displayName = "Nova",
        arabicName = "نوفا (طبيعي متوازن)",
        descriptionEn = "Warm, expressive, natural conversational cadence",
        descriptionAr = "نبرة دافئة وطبيعية متوازنة وسلسة للمحادثة",
        pitch = 1.03f,
        speechRate = 1.12f,
        preferredGender = "female"
    ),
    ECHO(
        id = "echo",
        displayName = "Echo",
        arabicName = "إيكو (عميق وواثق)",
        descriptionEn = "Rich, grounded, articulate male timbre",
        descriptionAr = "صوت رجالي رصين وواضح بإيقاع بشري طبيعي",
        pitch = 0.90f,
        speechRate = 1.08f,
        preferredGender = "male"
    ),
    SHIMMER(
        id = "shimmer",
        displayName = "Shimmer",
        arabicName = "شيمر (حيوي ومشرق)",
        descriptionEn = "Lively, crisp, upbeat human voice",
        descriptionAr = "صوت حيوي ومشرق فائق الوضوح والانطلاق",
        pitch = 1.08f,
        speechRate = 1.15f,
        preferredGender = "female"
    ),
    ONYX(
        id = "onyx",
        displayName = "Onyx",
        arabicName = "أونيكس (هادئ وسلس)",
        descriptionEn = "Smooth, warm, thoughtful narrator",
        descriptionAr = "راوي سلس وهادئ بنبرة إنسانية مريحة",
        pitch = 0.96f,
        speechRate = 1.05f,
        preferredGender = "neutral"
    );

    companion object {
        fun fromId(id: String): AiVoiceProfile =
            entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: NOVA
    }
}

class GamingTtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    private var currentIsArabic = false
    private var pendingRestoreProfile: AiVoiceProfile? = null

    private val _isSpeakingState = MutableStateFlow(false)
    val isSpeakingState: StateFlow<Boolean> = _isSpeakingState.asStateFlow()

    private val _activeVoiceName = MutableStateFlow("Natural Human Voice")
    val activeVoiceName: StateFlow<String> = _activeVoiceName.asStateFlow()

    var currentProfile: AiVoiceProfile = AiVoiceProfile.NOVA
        private set

    var onSpeechCompleted: (() -> Unit)? = null

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            setupHumanVoice(isArabic = currentIsArabic)

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeakingState.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeakingState.value = false
                    restorePreviewProfileIfNeeded()
                    onSpeechCompleted?.invoke()
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeakingState.value = false
                    restorePreviewProfileIfNeeded()
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    _isSpeakingState.value = false
                    restorePreviewProfileIfNeeded()
                }
            })
        } else {
            Log.e("GamingTtsManager", "TTS initialization failed: $status")
        }
    }

    private fun restorePreviewProfileIfNeeded() {
        val restore = pendingRestoreProfile ?: return
        pendingRestoreProfile = null
        currentProfile = restore
        if (isInitialized) {
            setupHumanVoice(isArabic = currentIsArabic)
        }
    }

    fun setLanguageArabic(isArabic: Boolean) {
        currentIsArabic = isArabic
        if (isInitialized) {
            setupHumanVoice(isArabic = isArabic)
        }
    }

    fun setVoiceProfile(profile: AiVoiceProfile) {
        pendingRestoreProfile = null
        currentProfile = profile
        if (isInitialized) {
            setupHumanVoice(isArabic = currentIsArabic)
        }
    }

    @Suppress("DEPRECATION")
    fun setupHumanVoice(isArabic: Boolean) {
        currentIsArabic = isArabic
        if (!isInitialized || tts == null) return
        try {
            val targetLocale = if (isArabic) Locale.forLanguageTag("ar-EG") else Locale.US
            val result = tts?.setLanguage(targetLocale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                if (isArabic) tts?.setLanguage(Locale.forLanguageTag("ar"))
                else tts?.setLanguage(Locale.US)
            }

            // Natural conversational pitch & brisk crisp cadence
            tts?.setPitch(currentProfile.pitch)
            tts?.setSpeechRate(currentProfile.speechRate)

            val availableVoices = tts?.voices
            if (!availableVoices.isNullOrEmpty()) {
                val candidateVoices = availableVoices.filter { voice ->
                    val langMatch = if (isArabic) {
                        voice.locale.language.startsWith("ar", ignoreCase = true)
                    } else {
                        voice.locale.language.startsWith("en", ignoreCase = true)
                    }
                    val notBlocked = !voice.features.orEmpty().contains("notInstalled")
                    langMatch && notBlocked
                }

                val genderPref = currentProfile.preferredGender
                val bestVoice = candidateVoices.maxByOrNull { voice ->
                    var score = 0
                    score += when {
                        voice.quality >= Voice.QUALITY_VERY_HIGH -> 600
                        voice.quality >= Voice.QUALITY_HIGH -> 420
                        voice.quality >= Voice.QUALITY_NORMAL -> 200
                        else -> 50
                    }

                    // Prefer local low-latency voices over high-latency network voices that pause or clip
                    if (!voice.isNetworkConnectionRequired) {
                        score += 280
                    } else if (voice.latency <= Voice.LATENCY_NORMAL) {
                        score += 120
                    }

                    val name = voice.name.lowercase()
                    if (name.contains("neural")) score += 450
                    if (name.contains("wavenet")) score += 420
                    if (name.contains("natural")) score += 380
                    if (name.contains("studio")) score += 350
                    if (name.contains("google")) score += 160
                    if (!isArabic && voice.locale.country.equals("US", ignoreCase = true)) score += 140
                    if (isArabic && voice.locale.country.equals("EG", ignoreCase = true)) score += 180

                    when (genderPref) {
                        "female" -> {
                            if (name.contains("female") || name.contains("sfg") || name.contains("iob") ||
                                name.contains("tpf") || name.contains("sfb") ||
                                name.contains("-a-") || name.contains("-c-") || name.contains("-e-") || name.contains("-f-")
                            ) {
                                score += 320
                            }
                        }
                        "male" -> {
                            if (name.contains("male") || name.contains("smb") || name.contains("iom") ||
                                name.contains("tpm") || name.contains("-b-") || name.contains("-d-") || name.contains("-g-")
                            ) {
                                score += 320
                            }
                        }
                        else -> {
                            score += 100
                        }
                    }
                    score
                }

                if (bestVoice != null) {
                    tts?.voice = bestVoice
                    _activeVoiceName.value = "${currentProfile.displayName} (${bestVoice.name.substringAfterLast("-", "HD")})"
                } else {
                    _activeVoiceName.value = currentProfile.displayName
                }
            }
        } catch (e: Exception) {
            Log.w("GamingTtsManager", "Error tuning human voice: ${e.message}")
        }
    }

    /**
     * Speaks full text seamlessly without truncation, preserving natural human phrasing and pauses.
     */
    fun speak(text: String, utteranceId: String = "rabbit_human_speech") {
        if (!isInitialized || tts == null) {
            Log.w("GamingTtsManager", "TTS not initialized")
            return
        }
        stop()

        val isArabicText = text.any { it in '\u0600'..'\u06FF' }
        setupHumanVoice(isArabic = isArabicText)

        val cleanSpeechText = cleanMarkdownForHumanSpeech(text, isArabicText)
        if (cleanSpeechText.isBlank()) return

        val maxLen = try {
            TextToSpeech.getMaxSpeechInputLength().coerceAtMost(3500)
        } catch (_: Exception) {
            3500
        }

        if (cleanSpeechText.length <= maxLen) {
            tts?.speak(cleanSpeechText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } else {
            val chunks = splitIntoSpeechChunks(cleanSpeechText, maxLen)
            for (i in chunks.indices) {
                val queueMode = if (i == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                val chunkId = if (i == chunks.lastIndex) utteranceId else "${utteranceId}_chunk_$i"
                tts?.speak(chunks[i], queueMode, null, chunkId)
            }
        }
    }

    fun previewVoice(profile: AiVoiceProfile, sampleText: String) {
        val originalProfile = pendingRestoreProfile ?: currentProfile
        pendingRestoreProfile = originalProfile
        currentProfile = profile
        val isArabicText = sampleText.any { it in '\u0600'..'\u06FF' }
        setupHumanVoice(isArabic = isArabicText)
        speak(sampleText, "rabbit_preview_${profile.id}")
    }

    private fun splitIntoSpeechChunks(fullText: String, maxChunkSize: Int): List<String> {
        val result = mutableListOf<String>()
        val paragraphs = fullText.split("\n\n", "\n")
        var currentChunk = StringBuilder()

        for (para in paragraphs) {
            val trimmed = para.trim()
            if (trimmed.isEmpty()) continue

            if (currentChunk.length + trimmed.length + 2 > maxChunkSize) {
                if (currentChunk.isNotEmpty()) {
                    result.add(currentChunk.toString())
                    currentChunk = StringBuilder()
                }
                if (trimmed.length > maxChunkSize) {
                    val sentences = trimmed.split(Regex("(?<=[.!?؛؟])\\s+"))
                    for (sentence in sentences) {
                        if (currentChunk.length + sentence.length + 2 > maxChunkSize) {
                            if (currentChunk.isNotEmpty()) {
                                result.add(currentChunk.toString())
                                currentChunk = StringBuilder()
                            }
                        }
                        if (currentChunk.isNotEmpty()) currentChunk.append(" ")
                        currentChunk.append(sentence)
                    }
                } else {
                    currentChunk.append(trimmed)
                }
            } else {
                if (currentChunk.isNotEmpty()) currentChunk.append(". ")
                currentChunk.append(trimmed)
            }
        }

        if (currentChunk.isNotEmpty()) {
            result.add(currentChunk.toString())
        }
        return if (result.isEmpty()) listOf(fullText) else result
    }

    private fun cleanMarkdownForHumanSpeech(raw: String, isArabic: Boolean): String {
        var text = raw
            .replace(Regex("```[\\s\\S]*?```"), " ")
            .replace(Regex("\\[(.*?)\\]\\(.*?\\)"), "$1")
            .replace(Regex("[#*`_~]"), "")
            .replace("---", ", ")
            // Natural pause after list numbers without English "Step X:" intruding on Arabic
            .replace(Regex("(?m)^(\\d+)\\.\\s*"), "$1, ")
            .replace(Regex("(?m)^[-•▪▸]\\s+"), ", ")
            // Remove emojis & box-drawing symbols
            .replace(Regex("[🚀⚡🛴📍🔓✅❌⚠️🎙️⏹️💡✨🔍🛠️🏆🎯🔹🌟]"), "")

        text = if (isArabic) {
            text.replace("km/h", " كيلومتر في الساعة ")
                .replace("EGP", " جنيه مصري ")
        } else {
            text.replace("km/h", " kilometers per hour ")
                .replace("EGP", " Egyptian pounds ")
        }

        return text
            .replace(Regex("\\n+"), ". ")
            .replace(Regex("\\.\\s*\\."), ".")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    var isBackgroundStreamingEnabled: Boolean = true

    fun bindLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                if (!isBackgroundStreamingEnabled) {
                    stop()
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                if (!isBackgroundStreamingEnabled) {
                    stop()
                }
            }

            override fun onDestroy(owner: LifecycleOwner) {
                shutdown()
            }
        })
    }

    fun stop() {
        if (tts != null) {
            tts?.stop()
            _isSpeakingState.value = false
        }
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
