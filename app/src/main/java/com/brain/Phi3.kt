package com.brain

import ai.onnxruntime.*
import java.io.File

class Phi3(val basePath: String) {

    private var session: OrtSession
    private var env: OrtEnvironment =
        OrtEnvironment.getEnvironment()

    private var tokenizer = Tokenizer(basePath)

    init {
        session = env.createSession(
            File(basePath + "/model.onnx").absolutePath,
            OrtSession.SessionOptions()
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
}
