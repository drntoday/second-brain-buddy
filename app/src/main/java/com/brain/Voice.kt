package com.brain

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.content.Intent

class Voice(val ctx: Context) {

      private val sr = SpeechRecognizer.createSpeechRecognizer(ctx)

          fun listen(cb: (String) -> Unit) {

                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

                                                sr.setRecognitionListener(object : RecognitionListener {

                                                              override fun onResults(results: Bundle?) {
                                                                                val list = results
                                                                                                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                                                                                                                    if (!list.isNullOrEmpty()) {
                                                                                                                                          cb(list[0])
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
