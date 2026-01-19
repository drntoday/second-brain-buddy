package com.brain

class Tone {

    fun makeHinglish(text: String): String {

        return "Dost, " + text
            .replace("please", "please yaar")
            .replace("hello", "arre suno")
    }
}
