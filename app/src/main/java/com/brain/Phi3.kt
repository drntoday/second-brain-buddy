package com.brain

class Phi3 {

    val lang = Language()

    fun reply(input: String): String {

        val language = lang.detect(input)

        val base = when(language){

            "hindi" -> """
                Main Solmie hoon – tumhara dost.
                Aaram se, friendly tarike se jawab do.
                Simple shabdon me samjhao.
            """

            "bhojpuri" -> """
                Hum Solmie bani.
                Pyaar se, aasan boli me samjha.
            """

            "punjabi" -> """
                Main Solmie haan.
                Dost waang gal karo, sokhi Punjabi.
            """

            "haryanvi" -> """
                Main Solmie su.
                Apne dhang te, mithe bol me bata.
            """

            else -> """
                I am Solmie, your friendly buddy.
                Reply in warm, simple, human tone.
            """
        }

        // REAL MODEL WILL COME HERE
        return base + "\n\n" + "You said: " + input
    }
}
