package com.brain

import android.content.Context
import kotlin.random.Random

class Phi3(private val ctx: Context) {

    private val fillers = listOf(
        "Samajh gaya dost.",
        "Theek hai, sun raha hoon.",
        "Accha sawal hai.",
        "Haan, bataata hoon.",
        "Sochne do zara."
    )

    fun reply(userText: String): String {

        // 🧠 Simulate thinking (important for mic stability)
        Thread.sleep(700)

        val filler = fillers.random()

        return "$filler Aapne kaha: \"$userText\".\n" +
               "Abhi main basic mode mein hoon, lekin dheere dheere aur smart ban jaunga 🙂"
    }
}
