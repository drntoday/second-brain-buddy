package com.brain

class Whisper {

    fun short(text: String): String {

        val lines = text.split(" ")

        return if(lines.size > 30)
            lines.take(20).joinToString(" ") + "..."
        else text
    }
}
