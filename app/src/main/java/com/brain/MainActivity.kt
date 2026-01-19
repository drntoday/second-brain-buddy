package com.brain

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {

    lateinit var speakBtn: Button
    lateinit var output: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speakBtn = Button(this)
        output = TextView(this)

        speakBtn.text = "Speak"

        speakBtn.setOnClickListener {
            output.text = "Listening..."
        }

        setContentView(output)
    }
}
