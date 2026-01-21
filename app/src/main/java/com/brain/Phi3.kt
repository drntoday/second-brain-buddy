package com.brain

import android.content.Context
import ai.onnxruntime.*
import ai.onnxruntime.genai.*
import java.io.File

class Phi3(private val ctx: Context) {

    private val modelDir: File =
        File(ctx.filesDir, "phi35")

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val tokenizer: Tokenizer
    private val generator: Generator

    init {
        // --- Load ONNX model ---
        val modelPath = File(modelDir, "model.onnx").absolutePath

        val opts = OrtSession.SessionOptions().apply {
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
            setIntraOpNumThreads(4)
        }

        session = env.createSession(modelPath, opts)

        // --- Load tokenizer ---
        val tokenizerPath = File(modelDir, "tokenizer.json").absolutePath
        tokenizer = Tokenizer.fromFile(tokenizerPath)

        // --- Create generator ---
        val genConfigPath = File(modelDir, "genai_config.json").absolutePath
        val genConfig = GeneratorConfig.fromFile(genConfigPath)

        generator = Generator(session, genConfig)
    }

    /**
     * Main inference call
     * Safe, blocking, deterministic
     */
    fun reply(prompt: String): String {

        // ---- Prompt formatting (VERY IMPORTANT) ----
        val fullPrompt = """
            <|system|>
            You are Solmie, a friendly helpful assistant.
            Reply naturally, briefly, and clearly.
            <|user|>
            $prompt
            <|assistant|>
        """.trimIndent()

        // ---- Tokenize ----
        val inputTokens = tokenizer.encode(fullPrompt)

        generator.appendTokens(inputTokens)

        val output = StringBuilder()

        // ---- Generate tokens ----
        while (!generator.isDone) {
            val token = generator.generateNextToken()
            val text = tokenizer.decode(listOf(token))
            output.append(text)
        }

        generator.reset()

        return output.toString()
            .replace("<|assistant|>", "")
            .trim()
    }
}
