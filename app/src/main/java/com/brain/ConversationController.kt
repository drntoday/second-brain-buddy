package com.brain

import android.content.Context
import android.os.Handler
import android.os.Looper

class ConversationController(
    private val memory: ShortTermMemory = ShortTermMemory(50),
    private val ctx: Context,
    private val speech: SpeechController
) {

    private val ui = Handler(Looper.getMainLooper())

    private val voice = Voice(ctx)
    private val phi = Phi3(ctx)
    private val search = Search()
    private lateinit var wake: WakeListener
    private var inConversation = false

    /* -------------------- WAKE MODE -------------------- */

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

    /* -------------------- CONVERSATION -------------------- */

    private fun startConversation() {
        inConversation = true
        wake.stop()

        speech.speak(
            text = "Haan dost, bolo kya madad karun?"
        ) {
            listenOnce()
        }
    }

    private fun listenOnce() {
        voice.listen { text ->

            val lang = LanguageDetector.detect(text)
            memory.addUser(text)

            Thread {
                // 🔍 Decide if search is needed
                val webInfo = if (needsSearch(text)) {
                    search.web(text)
                } else ""

                val wikiInfo = if (needsWiki(text)) {
                    search.wiki(text)
                } else ""

                val prompt = """
                    ${lang.prompt}

                    Conversation so far:
                    ${memory.context()}

                    External knowledge (if useful):
                    Web: $webInfo
                    Wiki: $wikiInfo

                    Now reply clearly and naturally.
                """.trimIndent()

                val answer = phi.reply(prompt)
                memory.addAssistant(answer)

                ui.post {
                    speech.setLanguage(lang)

                    val chunks = TextChunker.chunk(answer)
                    speakChunksSequentially(chunks) {
                        startWakeMode()
                    }
                }
            }.start()
        }
    }

    /* -------------------- STREAMING SPEECH -------------------- */

    private fun speakChunksSequentially(
        chunks: List<String>,
        onComplete: () -> Unit
    ) {
        if (chunks.isEmpty()) {
            onComplete()
            return
        }

        val iterator = chunks.iterator()

        fun speakNext() {
            if (!iterator.hasNext()) {
                onComplete()
                return
            }

            speech.speak(iterator.next()) {
                speakNext()
            }
        }

        speakNext()
    }

    /* -------------------- LIFECYCLE -------------------- */

    fun stop() {
        if (::wake.isInitialized) {
            wake.stop()
        }
    }
}
