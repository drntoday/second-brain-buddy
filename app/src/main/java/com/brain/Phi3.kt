package com.brain

class Phi3 {

    val lang = Language()

    fun reply(input: String): String {

        val language = lang.detect(input)

        val prompt = lang.systemPrompt(language)

        // Here real Phi-3 will run
        return "$prompt → $input"
    }
}
