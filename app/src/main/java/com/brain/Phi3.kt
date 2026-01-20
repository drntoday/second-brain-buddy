package com.brain

import android.content.Context
import com.microsoft.onnxruntime.genai.*

class Phi3(val ctx: Context) {

    var model: Model
    var tokenizer: Tokenizer

    init {
        val path = ctx.filesDir.absolutePath + "/phi35"
        model = Model(path)
        tokenizer = model.createTokenizer()
    }

    fun reply(prompt: String): String {

        val params = GeneratorParams(model)
        params.setInput(prompt)

        val generator = Generator(model, params)

        var result = ""

        while (!generator.isDone) {
            generator.computeLogits()
            generator.generateNextToken()

            result += generator.getLastTokenInText()
        }

        return result
    }
}
