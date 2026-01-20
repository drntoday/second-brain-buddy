package com.brain

import ai.onnxruntime.genai.*

class Phi3(modelPath: String) {

    private val model: Model
    private val tokenizer: Tokenizer

    init {
        // Microsoft recommended loader
        model = Model(modelPath)
        tokenizer = model.createTokenizer()
    }

    // -------------------------------
    // SIMPLE REPLY
    // -------------------------------

    fun reply(input: String): String {

        val prompt =
            "<|user|>\n$input\n<|assistant|>"

        val params = GeneratorParams(model)

        // official search options
        params.setSearchOption("max_length", 256)
        params.setSearchOption("temperature", 0.7)

        // tokenize using MICROSOFT tokenizer
        params.inputSequences =
            tokenizer.encode(prompt)

        val generator =
            Generator(model, params)

        val output = StringBuilder()

        while (generator.hasNext()) {

            generator.computeLogits()
            generator.generateNextToken()

            val token =
                generator.getLastTokenInSequence(0)

            output.append(
                tokenizer.decode(token)
            )
        }

        return output.toString()
    }

    // -------------------------------
    // STREAMING (OFFICIAL WAY)
    // -------------------------------

    fun replyStream(
        input: String,
        onToken: (String) -> Unit
    ) {

        val prompt =
            "<|user|>\n$input\n<|assistant|>"

        val params = GeneratorParams(model)

        params.setSearchOption("max_length", 256)

        params.inputSequences =
            tokenizer.encode(prompt)

        val generator =
            Generator(model, params)

        while (generator.hasNext()) {

            generator.computeLogits()
            generator.generateNextToken()

            val token =
                generator.getLastTokenInSequence(0)

            onToken(
                tokenizer.decode(token)
            )
        }
    }
}
