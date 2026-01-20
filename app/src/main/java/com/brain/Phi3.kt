package com.brain

import ai.onnxruntime.*
import java.io.File

class Phi3 {

    var session: OrtSession? = null
    val lang = Language()

    fun load(path: String) {

        val env = OrtEnvironment.getEnvironment()

        session = env.createSession(
            File(path).absolutePath,
            OrtSession.SessionOptions()
        )
    }

    fun reply(input: String): String {

        val language = lang.detect(input)

        val prompt = lang.systemPrompt(language) +
                     "\nUser: " + input +
                     "\nSolmie:"

        return runModel(prompt)
    }

    private fun runModel(text: String): String {

        try {

            // SIMPLE PLACEHOLDER TOKEN LOGIC
            // Real tokenizer we add next

            val result = session?.run(
                mapOf("input" to OnnxTensor.createTensor(
                    OrtEnvironment.getEnvironment(),
                    arrayOf(text)
                ))
            )

            return result?.get(0)?.value.toString()

        } catch(e: Exception) {

            return "Dost, thoda soch raha hoon…"
        }
    }
}
