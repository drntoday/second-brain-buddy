package com.brain

class Tutor {

    fun teach(topic: String): String {

        return """
        Dost, main simple me samjhata hoon:

        1) Chhota explanation:
        $topic ko aise samjho jaise real life story.

        2) Example:
        Socho tum apne dost ko samjha rahe ho.

        3) Ab ek chhota question:
        Tum apne words me isko kaise bologe?
        """
    }

    fun isStudy(text: String): Boolean {

        val keys = listOf(
            "samjhao",
            "sikhao",
            "explain",
            "padhai",
            "teach",
            "samajh"
        )

        return keys.any { text.contains(it) }
    }
}
