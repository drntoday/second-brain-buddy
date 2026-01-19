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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        speakBtn = findViewById(R.id.speak)
        output = findViewById(R.id.output)

        speakBtn.setOnClickListener {

            val userText = "hello solmie"

            val ai = phi.reply(userText)

            output.text = ai
        }
    }
}
