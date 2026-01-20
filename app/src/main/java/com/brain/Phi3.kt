package com.brain

import ai.onnxruntime.*
import java.io.File

class Phi3(val basePath: String) {

    private var session: OrtSession
    private var env: OrtEnvironment =
        OrtEnvironment.getEnvironment()

    private var tokenizer = Tokenizer(basePath)

    init {
        // 🔥 ADDED: Configure thread options
        val options = OrtSession.SessionOptions()
        
        options.setIntraOpNumThreads(4)
        options.setInterOpNumThreads(4)
        
        session = env.createSession(
            File(basePath + "/model.onnx").absolutePath,
            options
        )
    }

    fun reply(input: String): String {

        val prompt =
            "<|user|>\n$input\n<|assistant|>"

        val tokens = tokenizer.encode(prompt)

        val tensor = OnnxTensor.createTensor(
            env,
            longArrayOf(1, tokens.size.toLong()),
            tokens
        )

        val result = session.run(
            mapOf("input_ids" to tensor)
        )

        val output =
            result.get(0).value as Array<LongArray>

        return tokenizer.decode(output[0])
    }

    // 🔥 ADDED: Streaming function
    fun replyStream(
        input: String,
        onToken: (String) -> Unit
    ) {

        val prompt =
            "<|user|>\n$input\n<|assistant|>"

        val tokens = tokenizer.encode(prompt)

        val tensor = OnnxTensor.createTensor(
            env,
            longArrayOf(1, tokens.size.toLong()),
            tokens
        )

        val result = session.run(
            mapOf("input_ids" to tensor)
        )

        val output =
            result.get(0).value as Array<LongArray>

        for(t in output[0]) {

            val word = tokenizer.decode(longArrayOf(t))

            onToken(word)
        }
    }
}
