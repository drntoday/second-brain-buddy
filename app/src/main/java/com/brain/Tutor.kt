package com.brain

class Tutor(val phi: Phi3) {

    fun lesson(topic: String): String {

        val prompt = """
        You are a friendly tutor.
        Teach in this format:

        1) Simple explanation in Hinglish  
        2) Real life example  
        3) 2 small questions

        Topic: $topic
        """.trimIndent()

        return phi.reply(prompt)
    }

    fun evaluate(topic: String, answer: String): String {

        val prompt = """
        Student was asked about: $topic
        Student answered: $answer

        Give:
        - short feedback  
        - correct explanation  
        - motivation in Hinglish
        """.trimIndent()

        return phi.reply(prompt)
    }
}
