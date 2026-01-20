package com.brain

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent

class WakeListener : Service(), RecognitionListener {

    lateinit var sr: SpeechRecognizer

    val words = listOf(
        "solmie", "solmi", "सोमी", "सोल्मी"
    )

    override fun onCreate() {
        super.onCreate()

        sr = SpeechRecognizer.createSpeechRecognizer(this)
        sr.setRecognitionListener(this)

        listen()
    }

    fun listen() {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

        i.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            "en-IN"
        )

        i.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        sr.startListening(i)
    }

    override fun onResults(r: Bundle?) {

        val list =
          r?.getStringArrayList(
            SpeechRecognizer.RESULTS_RECOGNITION
          ) ?: return

        val text = list.joinToString(" ").lowercase()

        for(w in words){
            if(text.contains(w)){
                startService(
                  Intent(this, WhisperService::class.java)
                )
                break
            }
        }

        listen()
    }

    override fun onError(e: Int) {
        listen()
    }

    override fun onReadyForSpeech(p0: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(p0: Float) {}
    override fun onBufferReceived(p0: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onPartialResults(p0: Bundle?) {}
    override fun onEvent(p0: Int, p1: Bundle?) {}

    override fun onBind(i: Intent?): IBinder? = null
}
