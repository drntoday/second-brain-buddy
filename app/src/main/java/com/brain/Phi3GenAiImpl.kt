package com.brain

import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.genai.*
import java.io.File

/**
 * This file ONLY loads if GenAI runtime exists.
 * Build-safe, runtime-isolated.
 */
internal class Phi3GenAiImpl(modelDir: File) {

    private val env = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val tokenizer: Tokenizer
    private val generator: Generator

    init {
        val modelPath = File(modelDir, "model.onnx").absolutePath

        val opts = OrtSession.SessionOptions().apply {
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
            setIntraOpNumThreads(4)
        }

        session = env.createSession(modelPath, opts)

        tokenizer = Tokenizer.fromFile(
            File(modelDir, "tokenizer.json").absolutePath
        )

        val genConfig = GeneratorConfig.fromFile(
            File(modelDir, "genai_config.json").absolutePath
        )

        generator = Generator(session, genConfig)
    }

    fun reply(prompt: String): String {

        val fullPrompt = """
            <|system|>
            You are Solmie, a friendly helpful assistant.
            Reply naturally and concisely.
            <|user|>
            $prompt
            <|assistant|>
        """.trimIndent()

        val inputTokens = tokenizer.encode(fullPrompt)
        generator.appendTokens(inputTokens)

        val out = StringBuilder()

        while (!generator.isDone) {
            val token = generator.generateNextToken()
            val text = tokenizer.decode(listOf(token)) ?: ""
            out.append(text)
        }

        generator.reset()

        return out.toString()
            .replace("<|assistant|>", "")
            .trim()
    }
}
