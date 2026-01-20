package com.brain

class Coach(val phi: Phi3) {

    fun improve(sentence: String): String {

        val prompt = """
        You are English conversation coach.

        User sentence: $sentence

        Provide:
        1) Better English version  
        2) Polite version  
        3) Hinglish explanation why

        Keep short.
        """.trimIndent()

        return phi.reply(prompt)
    }

    fun interview(q: String): String {

        val prompt = """
        Act as interview trainer.

        Question: $q

        Give:
        - model answer  
        - tips  
        - common mistakes

        in simple Hinglish
        """.trimIndent()

        return phi.reply(prompt)
    }
}
