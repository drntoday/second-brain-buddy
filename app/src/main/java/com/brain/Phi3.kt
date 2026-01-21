package com.brain

import android.content.Context
import ai.onnxruntime.*
import ai.onnxruntime.genai.*
import java.io.File

class Phi3(private val ctx: Context) {

    private val modelDir = File(ctx.filesDir, "phi35")

    private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
    private val session: OrtSession
    private val tokenizer: Tokenizer
    private val generator: Generator

    init {
        val modelPath = File(modelDir, "model.onnx").absolutePath
        val tokenizerPath = File(modelDir, "tokenizer.json").absolutePath
        val genConfigPath = File(modelDir, "genai_config.json").absolutePath

        require(File(modelPath).exists()) { "model.onnx missing" }
        require(File(tokenizerPath).exists()) { "tokenizer.json missing" }
        require(File(genConfigPath).exists()) { "genai_config.json missing" }

        val opts = OrtSession.SessionOptions().apply {
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
            setIntraOpNumThreads(4)
        }

        session = env.createSession(modelPath, opts)
        tokenizer = Tokenizer.fromFile(tokenizerPath)

        val genConfig = GeneratorConfig.fromFile(genConfigPath)
        generator = Generator(session, genConfig)
    }

    @Synchronized
    fun reply(prompt: String): String {

        val fullPrompt = """
            <|system|>
            You are Solmie, a friendly helpful assistant.
            Reply naturally, briefly, and clearly.
            <|user|>
            $prompt
            <|assistant|>
        """.trimIndent()

        val inputTokens = tokenizer.encode(fullPrompt)
        generator.appendTokens(inputTokens)

        val output = StringBuilder()

        while (!generator.isDone) {
            val token = generator.generateNextToken()
            val text = tokenizer.decode(listOf(token))
            output.append(text)

            if (output.length > 8000) break
        }

        generator.reset()

        return output.toString()
            .replace("<|assistant|>", "")
            .replace("<|end|>", "")
            .trim()
    }
}
