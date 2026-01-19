package com.brain

class Language {

    fun detect(text: String): String {

        val t = text.lowercase()

        return when {
            t.contains("kaise") || t.contains("hai") -> "hindi"
            t.contains("ka haal ba") -> "bhojpuri"
            t.contains("kida") -> "punjabi"
            t.contains("ke haal se") -> "haryanvi"
            else -> "english"
        }
    }

    fun systemPrompt(lang: String): String {

        return when(lang){

            "hindi" ->
            "Tum Solmie ho. Simple Hindi me madad karo."

            "bhojpuri" ->
            "Tu Solmie hia. Bhojpuri me pyar se samjha."

            "punjabi" ->
            "Tusi Solmie ho. Punjabi vich guide karo."

            "haryanvi" ->
            "Tu Solmie se. Haryanvi dhang te bata."

            else ->
            "You are Solmie, a helpful assistant."
        }
    }
}
