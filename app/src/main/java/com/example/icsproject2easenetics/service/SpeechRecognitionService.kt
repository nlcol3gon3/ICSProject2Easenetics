package com.example.icsproject2easenetics.service

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SpeechRecognitionService(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    private val _recognitionResult = MutableStateFlow<String?>(null)
    val recognitionResult: StateFlow<String?> = _recognitionResult

    private val _recognitionError = MutableStateFlow<String?>(null)
    val recognitionError: StateFlow<String?> = _recognitionError

    private val _isListening = MutableStateFlow(false)
    val isListeningState: StateFlow<Boolean> = _isListening

    private val _recognitionConfidence = MutableStateFlow<Float?>(null)
    val recognitionConfidence: StateFlow<Float?> = _recognitionConfidence

    init {
        initializeSpeechRecognizer()
    }

    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }
        } else {
            _recognitionError.value = "Speech recognition not available on this device"
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {
                _isListening.value = true
                _recognitionError.value = null
                _recognitionConfidence.value = null
            }

            override fun onBeginningOfSpeech() {
                // Speech detected, still listening
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Sound level changed, can be used for visual feedback
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // Audio buffer received
            }

            override fun onEndOfSpeech() {
                _isListening.value = false
            }

            override fun onError(error: Int) {
                _isListening.value = false
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognition service busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input detected"
                    else -> "Unknown recognition error"
                }
                _recognitionError.value = errorMessage
            }

            override fun onResults(results: android.os.Bundle?) {
                _isListening.value = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val confidenceScores = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)

                val bestMatch = matches?.getOrNull(0)
                val confidence = confidenceScores?.getOrNull(0)

                _recognitionResult.value = bestMatch ?: "No speech recognized"
                _recognitionConfidence.value = confidence

                if (confidence != null && confidence < 0.3f) {
                    _recognitionError.value = "Low confidence in recognition. Please speak clearly."
                }
            }

            override fun onPartialResults(partialResults: android.os.Bundle?) {
                // Partial results if supported
            }

            override fun onEvent(eventType: Int, params: android.os.Bundle?) {
                // Reserved for future use
            }
        }
    }

    fun startListening() {
        if (speechRecognizer == null) {
            _recognitionError.value = "Speech recognizer not available"
            return
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000)
            }

            speechRecognizer?.startListening(intent)
            isListening = true
        } catch (e: Exception) {
            _recognitionError.value = "Failed to start listening: ${e.message}"
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
        isListening = false
    }

    fun cancelListening() {
        speechRecognizer?.cancel()
        _isListening.value = false
        isListening = false
    }

    fun clearResult() {
        _recognitionResult.value = null
        _recognitionError.value = null
        _recognitionConfidence.value = null
    }

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}