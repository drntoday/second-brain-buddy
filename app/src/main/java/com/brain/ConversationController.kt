package com.brain

import android.content.Context
import android.os.Handler
import android.os.Looper

class ConversationController(
    private val ctx: Context,
    private val speech: SpeechController
) {

    private val ui = Handler(Looper.getMainLooper())

    private val voice = Voice(ctx)
    private val phi = Phi3(ctx)

    private lateinit var wake: WakeListener
    private var inConversation = false

    fun startWakeMode() {
        inConversation = false

        wake = WakeListener(ctx) {
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

        speech.speak(
            text = "Haan dost, bolo kya madad karun?",
        ) {
            listenOnce()
        }
    }

    private fun listenOnce() {
        voice.listen { text ->

            val lang = LanguageDetector.detect(text)
            val prompt = """
                ${lang.prompt}
                User said: "$text"
            """.trimIndent()

            Thread {
                val answer = phi.reply(prompt)

                ui.post {
                    speech.setLanguage(lang)
                    speech.speak(answer) {
                        startWakeMode()
                    }
                }
            }.start()
        }
    }

    fun stop() {
        if (::wake.isInitialized) wake.stop()
    }
}
