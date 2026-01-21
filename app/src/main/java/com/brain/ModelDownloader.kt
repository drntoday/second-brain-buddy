package com.brain

import android.content.Context
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class ModelDownloader(private val ctx: Context) {

    private val base =
        "https://huggingface.co/nvidia/Phi-3.5-mini-Instruct-ONNX-INT4/resolve/main/"

    private val files = listOf(
        "model.onnx",
        "model.onnx.data",
        "genai_config.json",
        "tokenizer.json",
        "tokenizer_config.json",
        "special_tokens_map.json"
    )

    fun modelDir(): File =
        File(ctx.filesDir, "phi35").apply { mkdirs() }

    fun isReady(): Boolean {
        val dir = modelDir()
        return files.all { File(dir, it).exists() && File(dir, it).length() > 1000 }
    }

    /** MUST be called from background thread */
    fun downloadAll(progress: (Int) -> Unit) {
        progress(0)

        download("model.onnx", 10)
        download("model.onnx_data", 50)
        renameUnderscore()
        download("genai_config.json", 70)
        download("tokenizer.json", 80)
        download("tokenizer_config.json", 90)
        download("special_tokens_map.json", 100)

        progress(100)
    }

    private fun download(name: String, progressMark: Int) {
        val out = File(modelDir(), name)
        if (out.exists() && out.length() > 1024 * 1024) return

        val url = URL(base + name)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout = 30_000

        conn.inputStream.use { input ->
            out.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun renameUnderscore() {
        val old = File(modelDir(), "model.onnx_data")
        val new = File(modelDir(), "model.onnx.data")
        if (old.exists() && !new.exists()) {
            old.renameTo(new)
        }
    }
}
