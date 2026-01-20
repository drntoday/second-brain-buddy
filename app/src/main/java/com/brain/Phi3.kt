package com.brain

import ai.onnxruntime.*
import java.io.File

class Phi3(val basePath: String) {

    private var session: OrtSession
    private var env: OrtEnvironment =
        OrtEnvironment.getEnvironment()

    private var tokenizer = Tokenizer(basePath)

    init {
        val options = OrtSession.SessionOptions()

        // Thread tuning for your phone
        options.setIntraOpNumThreads(4)
        options.setInterOpNumThreads(4)

        session = env.createSession(
            File(basePath + "/model.onnx").absolutePath,
            options
        )
    }

    // --------------------------------------------------
    // SINGLE REPLY (uses generation loop internally)
    // --------------------------------------------------

    fun reply(input: String): String {

        val prompt =
            "<|user|>\n$input\n<|assistant|>"

        val inputTokens = tokenizer.encode(prompt)

        var generated = mutableListOf<Long>()
        generated.addAll(inputTokens.toList())

        // ---- GENERATION LOOP ----
        repeat(120) {   // max tokens

            val tensor = OnnxTensor.createTensor(
                env,
                longArrayOf(1, generated.size.toLong()),
                generated.toLongArray()
            )

            val outputs = session.run(
                mapOf("input_ids" to tensor)
            )

            // logits shape: [1, seq, vocab]
            val logits =
                outputs.get(0).value as Array<Array<FloatArray>>

            // take last position logits
            val lastLogits =
                logits[0][logits[0].size - 1]

            val nextToken =
                argmax(lastLogits)

            // stop tokens
            if(nextToken == tokenizer.eos())
                return tokenizer.decode(
                    generated.toLongArray()
                )

            generated.add(nextToken)
        }

        return tokenizer.decode(
            generated.toLongArray()
        )
    }

    // --------------------------------------------------
    // STREAMING VERSION
    // --------------------------------------------------

    fun replyStream(
        input: String,
        onToken: (String) -> Unit
    ) {

        val prompt =
            "<|user|>\n$input\n<|assistant|>"

        val inputTokens = tokenizer.encode(prompt)

        var generated = mutableListOf<Long>()
        generated.addAll(inputTokens.toList())

        repeat(120) {

            val tensor = OnnxTensor.createTensor(
                env,
                longArrayOf(1, generated.size.toLong()),
                generated.toLongArray()
            )

            val outputs = session.run(
                mapOf("input_ids" to tensor)
            )

            val logits =
                outputs.get(0).value as Array<Array<FloatArray>>

            val lastLogits =
                logits[0][logits[0].size - 1]

            val nextToken =
                argmax(lastLogits)

            if(nextToken == tokenizer.eos())
                return

            generated.add(nextToken)

            // emit only new token
            onToken(
                tokenizer.decode(
                    longArrayOf(nextToken)
                )
            )
        }
    }

    // --------------------------------------------------

    private fun argmax(arr: FloatArray): Long {

        var best = 0
        var bestVal = arr[0]

        for(i in arr.indices) {
            if(arr[i] > bestVal) {
                bestVal = arr[i]
                best = i
            }
        }

        return best.toLong()
    }
}
