package com.brain

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import android.content.Intent

class WakeListener(val ctx: Context, val onWake: () -> Unit) {

        private val sr = SpeechRecognizer.createSpeechRecognizer(ctx)

            fun start() {

                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

                                sr.setRecognitionListener(object : RecognitionListener {

                                                override fun onResults(results: Bundle?) {
                                                                    val list = results
                                                                                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                                                                                                        val text = list?.joinToString(" ")?.lowercase() ?: ""

                                                                                                                        if (text.contains("solmie") ||
                                                                                                                                            text.contains("solmi") ||
                                                                                                                                                                text.contains("सोमी") ||
                                                                                                                                                                                    text.contains("सोल्मी")) {

                                                                                                                                                                                                            onWake()
                                                                                                                                                                                    }
                                                }

                                                            override fun onReadyForSpeech(p0: Bundle?) {}
                                                                        override fun onBeginningOfSpeech() {}
                                                                                    override fun onRmsChanged(p0: Float) {}
                                                                                                override fun onBufferReceived(p0: ByteArray?) {}
                                                                                                            override fun onEndOfSpeech() {}
                                                                                                                        override fun onError(p0: Int) {}
                                                                                                                                    override fun onPartialResults(p0: Bundle?) {}
                                                                                                                                                override fun onEvent(p0: Int, p1: Bundle?) {}
                                })

                                        sr.startListening(intent)
            }
}
