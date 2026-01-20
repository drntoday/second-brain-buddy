package com.brain

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class MainActivity : Activity() {

    lateinit var phi: Phi3
    lateinit var tv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tv = TextView(this)
        setContentView(tv)

        phi = Phi3(this)

        tv.text = "Solmie Ready – बोलो: Solmie"
    }
}
