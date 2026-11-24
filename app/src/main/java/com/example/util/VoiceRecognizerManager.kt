package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages on-demand single-shot speech-to-text microphone dictation from the Search Bar.
 */
class VoiceRecognizerManager(context: Context) {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private var recognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _rmsLevel = MutableStateFlow(0f)
    val rmsLevel: StateFlow<Float> = _rmsLevel.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentIsArabic: Boolean = false
    private var onFinalTextCallback: ((String) -> Unit)? = null
    private var onFallbackRequiredCallback: ((Intent) -> Unit)? = null

    fun isOnDeviceRecognitionAvailable(): Boolean {
        return try {
            SpeechRecognizer.isRecognitionAvailable(appContext)
        } catch (_: Exception) {
            false
        }
    }

    fun buildFallbackRecognizerIntent(isArabic: Boolean): Intent {
        val localeTag = if (isArabic) "ar-EG" else "en-US"
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeTag)
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                if (isArabic) "تحدث الآن إلى رابيت AI..." else "Speak to Rabbit AI..."
            )
        }
    }

    fun startListening(
        isArabic: Boolean,
        onFinalResult: (String) -> Unit,
        onSystemFallbackRequired: ((Intent) -> Unit)? = null
    ) {
        currentIsArabic = isArabic
        onFinalTextCallback = onFinalResult
        onFallbackRequiredCallback = onSystemFallbackRequired
        _errorMessage.value = null
        _partialText.value = ""

        mainHandler.post {
            if (!isOnDeviceRecognitionAvailable()) {
                _isListening.value = false
                if (onSystemFallbackRequired != null) {
                    onSystemFallbackRequired.invoke(buildFallbackRecognizerIntent(isArabic))
                } else {
                    _errorMessage.value = if (isArabic) {
                        "خدمة التعرف الصوتي غير متوفرة على هذا الجهاز"
                    } else {
                        "Speech recognition service unavailable on this device"
                    }
                }
                return@post
            }

            startRecognizerInternal()
        }
    }

    fun stopListening() {
        mainHandler.post {
            try {
                recognizer?.stopListening()
            } catch (_: Exception) {
            }
            _isListening.value = false
            _rmsLevel.value = 0f
        }
    }

    fun cancelListening() {
        mainHandler.post {
            stopInternalRecognizer()
            _partialText.value = ""
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun destroy() {
        val cleanup = Runnable { stopInternalRecognizer() }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            cleanup.run()
        } else {
            mainHandler.post(cleanup)
        }
    }

    private fun startRecognizerInternal() {
        try {
            recognizer?.destroy()
            recognizer = SpeechRecognizer.createSpeechRecognizer(appContext).apply {
                setRecognitionListener(createListener())
            }

            val localeTag = if (currentIsArabic) "ar-EG" else "en-US"
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, localeTag)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, localeTag)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1600L)
            }

            _isListening.value = true
            _rmsLevel.value = 0.22f
            recognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("VoiceRecognizer", "Failed to start SpeechRecognizer", e)
            _isListening.value = false
            _rmsLevel.value = 0f
            _errorMessage.value = e.localizedMessage
            onFallbackRequiredCallback?.invoke(buildFallbackRecognizerIntent(currentIsArabic))
        }
    }

    private fun stopInternalRecognizer() {
        try {
            recognizer?.cancel()
            recognizer?.destroy()
        } catch (_: Exception) {
        } finally {
            recognizer = null
            _isListening.value = false
            _rmsLevel.value = 0f
        }
    }

    private fun createListener(): RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            _isListening.value = true
            _errorMessage.value = null
        }

        override fun onBeginningOfSpeech() {
            _isListening.value = true
        }

        override fun onRmsChanged(rmsdB: Float) {
            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.15f, 1.0f)
            _rmsLevel.value = normalized
        }

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            _rmsLevel.value = 0.15f
        }

        override fun onError(error: Int) {
            _isListening.value = false
            _rmsLevel.value = 0f
            val msg = when (error) {
                SpeechRecognizer.ERROR_NO_MATCH ->
                    if (currentIsArabic) "لم يتم سماع كلام واضح، حاول مرة أخرى"
                    else "Didn't catch that. Tap mic to try again."
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                    if (currentIsArabic) "انتهى وقت الاستماع، حاول التحدث بوضوح"
                    else "Listening timed out. Please try again."
                SpeechRecognizer.ERROR_AUDIO ->
                    if (currentIsArabic) "تعذر الوصول إلى الميكروفون"
                    else "Microphone audio error"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                    if (currentIsArabic) "يرجى منح صلاحية الميكروفون"
                    else "Microphone permission required"
                SpeechRecognizer.ERROR_NETWORK,
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT ->
                    if (currentIsArabic) "يرجى التحقق من اتصال الشبكة للتعرف على الصوت"
                    else "Network error during voice recognition"
                SpeechRecognizer.ERROR_CLIENT -> null
                else ->
                    if (currentIsArabic) "تعذر التعرف على الصوت (كود $error)"
                    else "Voice input unavailable ($error)"
            }
            if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY || error == SpeechRecognizer.ERROR_SERVER) {
                onFallbackRequiredCallback?.invoke(buildFallbackRecognizerIntent(currentIsArabic))
            } else if (msg != null) {
                _errorMessage.value = msg
            }
        }

        override fun onResults(results: Bundle?) {
            _isListening.value = false
            _rmsLevel.value = 0f
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                .orEmpty()
            val best = matches.firstOrNull().orEmpty()
            _partialText.value = ""
            if (best.isNotBlank()) {
                onFinalTextCallback?.invoke(best)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                .orEmpty()
            val partial = matches.firstOrNull().orEmpty()
            if (partial.isNotBlank()) {
                _partialText.value = partial
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}
