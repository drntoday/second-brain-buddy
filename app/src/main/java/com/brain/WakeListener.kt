package com.brain

import android.content.Context
import android.content.Intent
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class WakeListener(val context: Context, val onWake: (String) -> Unit) {

    private var recognizer: SpeechRecognizer =
        SpeechRecognizer.createSpeechRecognizer(context)

    init {
        recognizer.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: android.os.Bundle?) {

                val matches =
                    results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                val text = matches?.get(0)?.lowercase() ?: ""

                // WAKE WORD DETECTION
                if (text.contains("solmie") ||
                    text.contains("solmi") ||
                    text.contains("सोमी") ||
                    text.contains("सोल्मी")) {

                    onWake(text)
                }

                startListening()
            }

            override fun onError(error: Int) {
                startListening()
            }

            override fun onReadyForSpeech(p0: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })
    }

    fun startListening() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "en-IN"
        )

        recognizer.startListening(intent)
    }

    fun stop() {
        recognizer.stopListening()
    }
}
