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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        speakBtn = findViewById(R.id.speak)
        output = findViewById(R.id.output)

        // initialize voice engine
        voice = Voice(this)

        speakBtn.setOnClickListener {

            // TEMP: real speech input will come next
            val userText = "hello solmie"

            // detect language
            val lang = language.detect(userText)

            // get AI reply from Phi-3 layer
            var ai = phi.reply(userText)

            // OPTIONAL: if user asks latest info → use search
            if(userText.contains("today") ||
               userText.contains("latest")) {

                ai = search.web(userText)
            }

            // show on screen
            output.text = ai

            // speak in detected language
            voice.speak(ai, lang)

            // save to memory
            memory.save(userText + " → " + ai)
        }
    }
}
