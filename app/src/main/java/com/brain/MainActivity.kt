package com.brain

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    lateinit var speakBtn: Button
    lateinit var output: TextView

    val phi = Phi3()
    val search = Search()
    val memory = Memory()
    lateinit var voice: Voice
    val language = Language()

    lateinit var wake: WakeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        speakBtn = findViewById(R.id.speak)
        output = findViewById(R.id.output)

        voice = Voice(this)

        // 🔥 WAKE WORD SYSTEM
        wake = WakeListener(this) { heard ->

            runOnUiThread {
                processSpeech(heard)
            }
        }

        wake.startListening()

        speakBtn.setOnClickListener {
            output.text = "Wake mode active: say SOLMIE"
        }
    }

    fun processSpeech(userText: String) {

        val lang = language.detect(userText)

        var ai = phi.reply(userText)

        // Use search for latest info
        if (userText.contains("today") ||
            userText.contains("latest") ||
            userText.contains("aaj") ||
            userText.contains("hun")) {

            ai = search.web(userText)
        }

        output.text = ai

        voice.speak(ai, lang)

        memory.save("$userText → $ai")
    }
}
