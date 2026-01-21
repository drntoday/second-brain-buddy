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

class Voice(private val ctx: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val ui = Handler(Looper.getMainLooper())

    fun listen(onResult: (String) -> Unit) {
        destroy()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(ctx)

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, ctx.packageName)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {

            override fun onResults(results: Bundle?) {
                val list =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (!list.isNullOrEmpty()) {
                    onResult(list[0])
                }

                destroy()
            }

            override fun onError(error: Int) {
                // Auto-recover after error
                destroy()
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)

        // Safety timeout: kill recognizer if stuck
        ui.postDelayed({
            destroy()
        }, 10_000)
    }

    private fun destroy() {
        speechRecognizer?.apply {
            stopListening()
            cancel()
            destroy()
        }
        speechRecognizer = null
    }
}
