package com.brain

class Safe {

    fun check(text: String): Boolean {

        val banned = listOf(
            "Kill",
            "Murder",
            "harm"
        )

        return banned.any {
            text.contains(it, true)
        }
    }

    fun message(): String =
        "Dost, main galat kaam me madad nahi kar sakta."
}
