package com.brain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

class WhisperService : Service() {

    private lateinit var speech: SpeechController
    private lateinit var downloader: ModelDownloader
    private var convo: ConversationController? = null

    private val ui = Handler(Looper.getMainLooper())
    private val started = AtomicBoolean(false)
    private val destroyed = AtomicBoolean(false)

    /* ---------------- SERVICE LIFECYCLE ---------------- */

    override fun onCreate() {
        super.onCreate()

        // Prevent double init
        if (started.getAndSet(true)) return

        createNotificationChannel()
        startForeground(1, buildNotification("Preparing Solmie brain…"))

        downloader = ModelDownloader(this)
        speech = SpeechController(this)

        // Init TTS first (fast)
        speech.initTts {
            prepareModel()
        }
    }

    override fun onDestroy() {
        destroyed.set(true)

        convo?.stop()
        convo = null

        speech.shutdown()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /* ---------------- MODEL PREP ---------------- */

    private fun prepareModel() {
        Thread {
            try {
                if (!downloader.isReady()) {
                    downloader.downloadAll { percent ->
                        ui.post {
                            updateNotification("Downloading Solmie brain… $percent%")
                        }
                    }
                }

                if (destroyed.get()) return@Thread

                ui.post {
                    updateNotification("Solmie is listening")

                    convo = ConversationController(
                        ctx = this,
                        speech = speech
                    )

                    convo?.startWakeMode()
                }

            } catch (e: Exception) {
                ui.post {
                    updateNotification("Solmie failed to initialize")
                }
            }
        }.start()
    }

    /* ---------------- NOTIFICATIONS ---------------- */

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
