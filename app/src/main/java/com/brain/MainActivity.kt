package com.brain

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var speakBtn: Button
    private lateinit var output: TextView

    private val phi = Phi3()
    private val search = Search()
    private val memory = Memory()
    private lateinit var voice: Voice
    private val language = Language()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // initialize voice engine with context
        voice = Voice(this)

        speakBtn = findViewById(R.id.speak)
        output = findViewById(R.id.output)

        speakBtn.setOnClickListener {

            // later this will come from speech-to-text
            val userText = "hello solmie"

            // get AI reply
            val aiReply = phi.reply(userText)

            // detect language
            val lang = language.detect(userText)

            // show on screen
            output.text = aiReply

            // speak result
            voice.speak(aiReply, lang)
        }
    }
}
