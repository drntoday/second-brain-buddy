package com.brain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import java.util.*

class WhisperService : Service() {

    lateinit var tts: TextToSpeech
    lateinit var phi: Phi3

    val ui = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = Notification.Builder(this, "solmie")
            .setContentTitle("Solmie Listening")
            .setContentText("Say Solmie to talk")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()

        // 🔐 Important – keeps service alive
        startForeground(1, notification)

        phi = Phi3(this)

        tts = TextToSpeech(this) {
            tts.language = Locale("hi", "IN")
            askUser()
        }
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            "solmie",
            "Solmie Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun askUser() {

        val v = Voice(this)

        speak("Haan dost, bolo kya madad karun?")

        v.listen { text ->

            val prompt = "Reply in friendly Hinglish: $text"

            Thread {
                val ans = phi.reply(prompt)

                ui.post {
                    speak(ans)

                    // 🔁 conversation loop
                    askUser()
                }

            }.start()
        }
    }

    fun speak(s: String) {

        val am = getSystemService(AUDIO_SERVICE) as AudioManager

        val isEar =
            am.isWiredHeadsetOn ||
            am.isBluetoothA2dpOn

        tts.setSpeechRate(if (isEar) 0.9f else 1.0f)

        tts.speak(
            s,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }

    override fun onBind(i: Intent?): IBinder? = null
}
