package com.brain

import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

class ConversationController(
    private val ctx: Context,
    private val speech: SpeechController,
    private val memory: ShortTermMemory = ShortTermMemory(50)
) {

    private val ui = Handler(Looper.getMainLooper())

    private val voice = Voice(ctx)
    private val phi = Phi3(ctx)

    private lateinit var wake: WakeListener

    private val inConversation = AtomicBoolean(false)
    private val isStreaming = AtomicBoolean(false)

    /* ---------------- WAKE MODE ---------------- */

    fun startWakeMode() {
        inConversation.set(false)

        wake = WakeListener(ctx) {
            ui.post {
                if (!inConversation.get()) {
                    startConversation()
                }
            }
        }
        wake.start()
    }

    /* ---------------- CONVERSATION ---------------- */

    private fun startConversation() {
        inConversation.set(true)
        wake.stop()

        speech.speak(
            text = "Haan dost, bolo kya madad karun?"
        ) {
            listenOnce()
        }
    }

    private fun listenOnce() {
        voice.listen { text ->

            // 🔥 Interrupt speech immediately if user speaks
            speech.interrupt()

            val lang = LanguageDetector.detect(text)
            memory.addUser(text)

            val prompt = """
                ${lang.prompt}

                Conversation so far:
                ${memory.context()}

                Now reply naturally and briefly.
            """.trimIndent()

            Thread {
                val answer = phi.reply(prompt)

                // If user interrupted during inference → ignore
                if (!inConversation.get()) return@Thread

                memory.addAssistant(answer)

                ui.post {
                    speech.setLanguage(lang)
                    streamAnswer(answer)
                }
            }.start()
        }
    }

    /* ---------------- STREAMING ---------------- */

    private fun streamAnswer(answer: String) {
        isStreaming.set(true)

        val chunks = TextChunker.chunk(answer)
        val iterator = chunks.iterator()

        fun speakNext() {
            if (!iterator.hasNext() || !isStreaming.get()) {
                isStreaming.set(false)
                startWakeMode()
                return
            }

            speech.speak(iterator.next()) {
                // If interrupted → stop streaming
                if (!speech.isSpeaking()) {
                    isStreaming.set(false)
                    startWakeMode()
                } else {
                    speakNext()
                }
            }
        }

        speakNext()
    }

    /* ---------------- STOP ---------------- */

    fun stop() {
        isStreaming.set(false)
        inConversation.set(false)

        speech.interrupt()

        if (::wake.isInitialized) {
            wake.stop()
        }
    }
}
