package com.example.icsproject2easenetics.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VoiceService(private val context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _speakingError = MutableStateFlow<String?>(null)
    val speakingError: StateFlow<String?> = _speakingError

    private val _currentUtterance = MutableStateFlow<String?>(null)
    val currentUtterance: StateFlow<String?> = _currentUtterance

    // Voice settings
    private var speechRate = 0.8f // Slower rate for older adults
    private var pitch = 1.0f

    init {
        initializeTTS()
    }

    private fun initializeTTS() {
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    _speakingError.value = "Text-to-speech language not supported"
                } else {
                    isInitialized = true
                    textToSpeech?.setSpeechRate(speechRate)
                    textToSpeech?.setPitch(pitch)
                    setupUtteranceListener()
                }
            } else {
                _speakingError.value = "Text-to-speech initialization failed"
            }
        }
    }

    private fun setupUtteranceListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
                _currentUtterance.value = utteranceId
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _currentUtterance.value = null
            }

            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _currentUtterance.value = null
                _speakingError.value = "Speech synthesis error"
            }
        })
    }

    fun speak(text: String, utteranceId: String = "default") {
        if (!isInitialized) {
            _speakingError.value = "Text-to-speech not initialized"
            return
        }

        try {
            // Clean text for better TTS
            val cleanText = cleanTextForTTS(text)
            textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            _speakingError.value = "Failed to speak text: ${e.message}"
        }
    }

    fun speakWithQueue(text: String, utteranceId: String = "default") {
        if (!isInitialized) return

        val cleanText = cleanTextForTTS(text)
        textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_ADD, null, utteranceId)
    }

    private fun cleanTextForTTS(text: String): String {
        return text
            .replace("#", "") // Remove markdown headers
            .replace("•", "• ") // Add space after bullet points
            .replace("**", "") // Remove bold markers
            .replace("\n", ". ") // Convert newlines to pauses
            .trim()
    }

    fun stopSpeaking() {
        textToSpeech?.stop()
        _isSpeaking.value = false
        _currentUtterance.value = null
    }

    fun setSpeechRate(rate: Float) {
        speechRate = rate.coerceIn(0.5f, 2.0f)
        textToSpeech?.setSpeechRate(speechRate)
    }

    fun setPitch(pitch: Float) {
        this.pitch = pitch.coerceIn(0.5f, 2.0f)
        textToSpeech?.setPitch(this.pitch)
    }

    fun getSpeechRate(): Float = speechRate
    fun getPitch(): Float = pitch

    // Accessibility features
    fun setSlowSpeed() {
        setSpeechRate(0.7f)
    }

    fun setNormalSpeed() {
        setSpeechRate(1.0f)
    }

    fun isSpeaking(): Boolean {
        return _isSpeaking.value
    }

    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        isInitialized = false
    }
}