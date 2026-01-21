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
    private val search = Search()

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

        speech.speak("Haan dost, bolo kya madad karun?") {
            listenOnce()
        }
    }

    /**
     * 🔥 STREAMING + SEARCH + MEMORY SAFE
     * (ONLY METHOD REPLACED)
     */
    private fun listenOnce() {
        voice.listen { text ->

            // 🔥 Immediate barge-in
            speech.interrupt()

            val lang = LanguageDetector.detect(text)
            memory.addUser(text)

            Thread {

                val grounding = if (shouldSearch(text)) {
                    fetchGrounding(text)
                } else {
                    ""
                }

                val prompt = buildPrompt(lang, grounding)

                val buffer = StringBuilder()
                isStreaming.set(true)

                phi.streamReply(prompt) { token ->

                    // ⛔ Stop immediately if interrupted
                    if (!inConversation.get() || !isStreaming.get()) {
                        false
                    } else {
                        buffer.append(token)

                        ui.post {
                            speech.setLanguage(lang)
                            speech.speak(token)
                        }

                        true
                    }
                }

                val finalAnswer = buffer.toString().trim()

                if (finalAnswer.isNotEmpty()) {
                    memory.addAssistant(finalAnswer)
                }

                ui.post {
                    isStreaming.set(false)
                    startWakeMode()
                }
            }.start()
        }
    }

    /* ---------------- SEARCH LOGIC ---------------- */

    private fun shouldSearch(text: String): Boolean {
        val t = text.lowercase()

        val triggers = listOf(
            "who", "what", "when", "where", "why", "how",
            "latest", "news", "current", "price",
            "explain", "define", "history", "meaning"
        )

        return triggers.any { t.contains(it) }
    }

    private fun fetchGrounding(query: String): String {
        val wiki = search.wiki(query)
        if (wiki.isNotBlank()) {
            return "Wikipedia summary:\n$wiki"
        }

        val web = search.web(query)
        return if (web.isNotBlank()) {
            "Web search results:\n$web"
        } else {
            ""
        }
    }

    private fun buildPrompt(
        lang: LanguageDetector.Lang,
        grounding: String
    ): String {
        return """
            ${lang.prompt}

            ${if (grounding.isNotBlank()) "Use the following real-world information:\n$grounding\n" else ""}

            Conversation so far:
            ${memory.context()}

            Now reply naturally, briefly, and clearly.
        """.trimIndent()
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
