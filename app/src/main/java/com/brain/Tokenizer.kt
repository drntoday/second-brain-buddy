package com.brain

import org.json.JSONObject
import java.io.File

class Tokenizer(path: String) {

    private var vocab: JSONObject

    init {
        val text = File(path + "/tokenizer.json").readText()
        vocab = JSONObject(text)
    }

    fun encode(text: String): LongArray {

        // Simple whitespace tokenizer (start)
        // Later we upgrade to full BPE

        val words = text.split(" ")

        return words.map {
            vocab.optLong(it, 1)
        }.toLongArray()
    }

    fun decode(tokens: LongArray): String {

        val map = vocab.toMap()

        return tokens.joinToString(" ") { t ->
            map[t.toString()]?.toString() ?: ""
        }
    }
}
