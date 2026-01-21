package com.brain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.os.Handler
import android.speech.tts.TextToSpeech
import java.util.*

class WhisperService : Service() {

    private lateinit var speech: SpeechController
    private lateinit var convo: ConversationController
    private lateinit var downloader: ModelDownloader

    private val ui = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(1, buildNotification("Preparing Solmie brain…"))

        downloader = ModelDownloader(this)

        speech = SpeechController(this)

        // Init TTS first
        speech.initTts {
            prepareModel()
        }
    }

    /* -------------------- MODEL -------------------- */

    private fun prepareModel() {
        Thread {
            if (!downloader.isReady()) {
                downloader.downloadAll { percent ->
                    ui.post {
                        updateNotification("Downloading Solmie brain… $percent%")
                    }
                }
            }

            ui.post {
                updateNotification("Solmie is listening")
                convo = ConversationController(
                    ctx = this,
                    speech = speech
                )
                convo.startWakeMode()
            }
        }.start()
    }

    /* -------------------- NOTIFICATION -------------------- */

    private fun buildNotification(text: String): Notification {
        return Notification.Builder(this, "solmie")
            .setContentTitle("Solmie – Second Brain")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(1, buildNotification(text))
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "solmie",
            "Solmie Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }

    override fun onDestroy() {
        convo.stop()
        speech.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
