package com.brain

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import java.util.*

class WhisperService : Service() {

    private lateinit var tts: TextToSpeech
    private lateinit var phi: Phi3
    private lateinit var voice: Voice
    private lateinit var wake: WakeListener

    private val ui = Handler(Looper.getMainLooper())
    private lateinit var audioManager: AudioManager
    private lateinit var focusRequest: AudioFocusRequest

    private var inConversation = false

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(1, buildNotification())

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        phi = Phi3(this)
        voice = Voice(this)

        wake = WakeListener(this) {
            ui.post {
                if (!inConversation) {
                    startConversation()
                }
            }
        }

        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale("hi", "IN")
                setTtsListener()
                startWakeMode()
            }
        }
    }

    /* -------------------- MODES -------------------- */

    private fun startWakeMode() {
        inConversation = false
        wake.start()
    }

    private fun startConversation() {
        inConversation = true
        wake.stop()

        speak("Haan dost, bolo kya madad karun?") {
            listenOnce()
        }
    }

    private fun listenOnce() {
        voice.listen { text ->
            val prompt = "Reply in friendly Hinglish: $text"

            Thread {
                val answer = phi.reply(prompt)
                ui.post {
                    speak(answer) {
                        // After response → go back to wake mode
                        startWakeMode()
                    }
                }
            }.start()
        }
    }

    /* -------------------- TTS -------------------- */

    private fun speak(text: String, onDone: (() -> Unit)? = null) {
        requestAudioFocus()

        pendingOnDone = onDone

        tts.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "SOLMIE_UTTERANCE"
        )
    }

    private var pendingOnDone: (() -> Unit)? = null

    private fun setTtsListener() {
        tts.setOnUtteranceProgressListener(
            SimpleUtteranceListener {
                ui.post {
                    abandonAudioFocus()
                    pendingOnDone?.invoke()
                    pendingOnDone = null
                }
            }
        )
    }

    /* -------------------- AUDIO -------------------- */

    private fun requestAudioFocus() {
        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .build()

        audioManager.requestAudioFocus(focusRequest)
    }

    private fun abandonAudioFocus() {
        audioManager.abandonAudioFocusRequest(focusRequest)
    }

    /* -------------------- NOTIFICATION -------------------- */

    private fun buildNotification(): Notification {
        return Notification.Builder(this, "solmie")
            .setContentTitle("Solmie is listening")
            .setContentText("Say \"Solmie\" to talk")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
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
        wake.stop()
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
