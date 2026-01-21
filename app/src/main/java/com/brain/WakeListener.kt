package com.brain

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.*

class WakeListener(
    private val ctx: Context,
    private val onWake: () -> Unit
) {

    private var recognizer: SpeechRecognizer? = null
    private val ui = Handler(Looper.getMainLooper())

    fun start() {
        stop()

        recognizer = SpeechRecognizer.createSpeechRecognizer(ctx)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ctx.packageName)
        }

        recognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                val list =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.map { it.lowercase(Locale.getDefault()) }
                        ?: emptyList()

                val hit = list.any {
                    it.contains("solmie") ||
                    it.contains("solmi") ||
                    it.contains("सोमी") ||
                    it.contains("सोल्मी")
                }

                if (hit) {
                    onWake()
                }

                restart()
            }

            override fun onError(error: Int) {
                restart()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        recognizer?.startListening(intent)
    }

    private fun restart() {
        ui.postDelayed({
            start()
        }, 500)
    }

    fun stop() {
        recognizer?.apply {
            stopListening()
            cancel()
            destroy()
        }
        recognizer = null
    }
}
