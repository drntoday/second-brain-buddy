package com.brain

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class Voice(context: Context) {

    var tts: TextToSpeech

    init {
        tts = TextToSpeech(context) {}
    }

    fun speak(text: String, lang: String) {

        val locale = when(lang){
            "hindi" -> Locale("hi","IN")
            "punjabi" -> Locale("pa","IN")
            else -> Locale.US
        }

        tts.language = locale
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}
