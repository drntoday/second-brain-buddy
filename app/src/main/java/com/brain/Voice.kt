package com.brain

import android.content.Context
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import java.util.*

class Voice(val context: Context) {

    var tts: TextToSpeech
    val audio =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        tts = TextToSpeech(context) {
            // ready
        }
    }

    fun speak(text: String, lang: String) {

        // --- WHISPER SETTINGS ---
        audio.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            3,              // low volume for privacy
            0
        )

        // prefer earphones
        audio.mode = AudioManager.MODE_IN_COMMUNICATION

        val locale = when(lang){
            "hindi" -> Locale("hi","IN")
            "punjabi" -> Locale("pa","IN")
            else -> Locale.US
        }

        tts.language = locale

        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_VOLUME] = "0.4"

        tts.speak(text,
            TextToSpeech.QUEUE_FLUSH,
            params)
    }
}
