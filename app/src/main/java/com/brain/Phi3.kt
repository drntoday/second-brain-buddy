package com.brain

import android.content.Context
import ai.onnxruntime.*
import ai.onnxruntime.genai.*
import java.io.File

class Phi3(private val ctx: Context) {

    private val modelDir = File(ctx.filesDir, "phi35")

    private val env = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val tokenizer: Tokenizer
    private val generator: Generator

    init {
        val opts = OrtSession.SessionOptions().apply {
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
            setIntraOpNumThreads(4)
        }

        session = env.createSession(
            File(modelDir, "model.onnx").absolutePath,
            opts
        )

        tokenizer = Tokenizer.fromFile(
            File(modelDir, "tokenizer.json").absolutePath
        )

        val genConfig = GeneratorConfig.fromFile(
            File(modelDir, "genai_config.json").absolutePath
        )

        generator = Generator(session, genConfig)
    }

    /* --------- NON-STREAMING (kept for safety) --------- */

    fun reply(prompt: String): String {
        val sb = StringBuilder()

        streamReply(prompt) { token ->
            sb.append(token)
            true
        }

        return sb.toString()
            .replace("<|assistant|>", "")
            .trim()
    }

    /* --------- STREAMING CORE --------- */

    fun streamReply(
        prompt: String,
        onToken: (String) -> Boolean
    ) {
        val fullPrompt = """
            <|system|>
            You are Solmie, a friendly helpful assistant.
            Reply naturally, briefly, and clearly.
            <|user|>
            $prompt
            <|assistant|>
        """.trimIndent()

        val tokens = tokenizer.encode(fullPrompt)
        generator.appendTokens(tokens)

        try {
            while (!generator.isDone) {
                val tokenId = generator.generateNextToken()
                val text = tokenizer.decode(listOf(tokenId))

                // Stop immediately if consumer says so
                if (!onToken(text)) break
            }
        } finally {
            generator.reset()
        }
    }
}
