package com.brain

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.IBinder
import android.speech.tts.TextToSpeech
import java.util.*

class WhisperService : Service() {

 lateinit var tts: TextToSpeech
 lateinit var phi: Phi3

 override fun onCreate() {
    super.onCreate()

    phi = Phi3(this)

    tts = TextToSpeech(this){
        tts.language = Locale("hi","IN")
        askUser()
    }
 }

 fun askUser(){

   val v = Voice(this)

   speak("Haan dost, bolo kya madad karun?")

   v.listen { text ->

      val prompt =
        "Reply in friendly Hinglish: $text"

      Thread{
        val ans = phi.reply(prompt)

        runOnUiThread {
          speak(ans)
        }

      }.start()
   }
 }

 fun speak(s: String){

   val am =
     getSystemService(AUDIO_SERVICE)
     as AudioManager

   val isEar =
     am.isWiredHeadsetOn ||
     am.isBluetoothA2dpOn

   if(isEar)
      tts.setSpeechRate(0.9f)
   else
      tts.setSpeechRate(1.0f)

   tts.speak(
     s,
     TextToSpeech.QUEUE_FLUSH,
     null,
     null
   )
 }

 override fun onBind(i: Intent?): IBinder? = null
}
