package com.brain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class WhisperService : Service() {

    private lateinit var conversation: WhisperConversationController
    private lateinit var audio: WhisperAudioController

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(1, buildNotification("Preparing Solmie brain…"))

        audio = WhisperAudioController(this)
        conversation = WhisperConversationController(
            service = this,
            audio = audio,
            onNotification = { updateNotification(it) }
        )

        audio.init {
            conversation.prepareModel()
        }
    }

    override fun onDestroy() {
        conversation.destroy()
        audio.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /* ---------------- NOTIFICATION ---------------- */

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
}
