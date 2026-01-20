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

    override fun onStartCommand(
     i: Intent?, f: Int, id: Int): Int {

     mode = i?.getStringExtra("mode") ?: "chat"
     return START_STICKY
        }
    
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

            val mem = Memory(this)
            val safe = Safe()
            val search = Search()

            when(mode){

             "tutor" -> {
               val t = Tutor(phi)
               speak("Kaun sa topic padhna hai dost?")
               v.listen { q -> speak(t.lesson(q)) }
             }

             "coach" -> {
               val c = Coach(phi)
               speak("Apna sentence bolo")
               v.listen { q -> speak(c.improve(q)) }
             }

             else -> {  // normal chat
               // existing logic
             }
            }

            // 🛡 SAFETY CHECK
            if(!safe.check(text)){
                speak("Sorry dost, is topic me main madad nahi kar sakta.")
                return@listen
            }

            // 🧠 MEMORY RECALL
            val past = mem.recall(text)

            val prompt = if(past != null)
                "Use this context: $past\nUser asked: $text"
            else
                text

            Thread {

                // 🤖 OFFLINE AI REPLY
                var ans = phi.reply(
                    safe.wrap("Reply in friendly Hinglish: $prompt")
                )

                // 🌐 ONLINE FALLBACK
                if(ans.length < 40){
                    val web = search.web(text)
                    if(web.isNotEmpty())
                        ans += "\n\nLatest info:\n$web"
                }

                v.listen { text ->

                    when {

                      text.startsWith("study") -> {

                        val t = Tutor(phi)
                        val ans = t.lesson(text.removePrefix("study"))
                        speak(ans)
                      }

                      text.startsWith("improve") -> {

                        val c = Coach(phi)
                        val ans = c.improve(text)
                        speak(ans)
                      }

                      else -> {
                        // normal earlier logic
                      }
                    }
                 }
                
                // 💾 SAVE TO MEMORY
                mem.add(text, ans)

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
