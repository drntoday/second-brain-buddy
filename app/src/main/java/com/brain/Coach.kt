package com.brain

class Coach {

    fun isCoach(text: String): Boolean {

        val keys = listOf(
            "reply",
            "kaise bolu",
            "interview",
            "answer",
            "improve english",
            "better sentence",
            "kya bolu",
            "baat kaise karu"
        )

        return keys.any { text.lowercase().contains(it) }
    }

    fun guide(text: String): String {

        return """
        Dost, main tumhari baat better bana deta hoon:

        1) Simple & clear line:
        → ${makeSentence(text)}

        2) Polite tone:
        → ${polite(text)}

        3) Confidence tip:
        → Dheere bolo, halka smile rakho.

        Chaho to practice karo, main sun raha hoon 👍
        """
    }

    private fun makeSentence(t: String): String {

        return t
            .replace("my", "My")
            .replace("i am", "I am")
            .replace("wanna", "want to")
    }

    private fun polite(t: String): String {
        return "I would like to say: " + t
    }
}
