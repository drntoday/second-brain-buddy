package com.brain

import android.content.Context
import android.os.Handler
import android.os.Looper

class WhisperConversationController(
    private val service: Context,
    private val audio: WhisperAudioController,
    private val onNotification: (String) -> Unit
) {

    private val ui = Handler(Looper.getMainLooper())

    private val voice = Voice(service)
    private val downloader = ModelDownloader(service)

    private lateinit var wake: WakeListener
    private lateinit var phi: Phi3

    private var modelReady = false
    private var inConversation = false

    /* ---------------- MODEL ---------------- */

    fun prepareModel() {
        Thread {
            if (!downloader.isReady()) {
                downloader.downloadAll { percent ->
                    ui.post {
                        onNotification("Downloading Solmie brain… $percent%")
                    }
                }
            }

            phi = Phi3(service)
            modelReady = true

            ui.post {
                onNotification("Solmie is listening")
                startWakeMode()
            }
        }.start()
    }

    /* ---------------- MODES ---------------- */

    private fun startWakeMode() {
        if (!modelReady) return

        inConversation = false

        wake = WakeListener(service) {
            ui.post {
                if (!inConversation) {
                    startConversation()
                }
            }
        }
        wake.start()
    }

    private fun startConversation() {
        inConversation = true
        wake.stop()

        audio.speak("Haan dost, bolo kya madad karun?") {
            listenOnce()
        }
    }

    private fun listenOnce() {
        voice.listen { text ->
            val prompt = "Reply in friendly Hinglish: $text"

            Thread {
                val answer = phi.reply(prompt)
                ui.post {
                    audio.speak(answer) {
                        startWakeMode()
                    }
                }
            }.start()
        }
    }

    fun destroy() {
        if (::wake.isInitialized) wake.stop()
    }
}
