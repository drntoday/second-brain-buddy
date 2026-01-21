package com.brain

import android.content.Context
import java.io.File
import java.net.URL

class ModelDownloader(private val ctx: Context) {

    private val base =
        "https://huggingface.co/nvidia/Phi-3.5-mini-Instruct-ONNX-INT4/resolve/main/"

    fun modelDir(): File {
        val f = File(ctx.filesDir, "phi35")
        if (!f.exists()) f.mkdirs()
        return f
    }

    fun isReady(): Boolean {
        val f = File(modelDir(), "model.onnx.data")
        return f.exists() && f.length() > 2_000_000_000
    }

    fun downloadAll(progress: (Int) -> Unit) {

        download("model.onnx", 0, 20, progress)

        download("model.onnx_data", 20, 70, progress)
        renameUnderscore()

        download("genai_config.json", 70, 80, progress)
        download("tokenizer.json", 80, 90, progress)
        download("tokenizer_config.json", 90, 95, progress)
        download("special_tokens_map.json", 95, 100, progress)

        progress(100)
    }

    private fun download(
        name: String,
        start: Int,
        end: Int,
        progress: (Int) -> Unit
    ) {
        val out = File(modelDir(), name)
        if (out.exists() && out.length() > 1000) return

        URL(base + name).openStream().use { input ->
            out.outputStream().use { output ->
                val buf = ByteArray(8 * 1024)
                var read: Int
                var total = 0L
                val sizeGuess = 500_000_000L

                while (input.read(buf).also { read = it } != -1) {
                    output.write(buf, 0, read)
                    total += read

                    val p = start + ((total * (end - start)) / sizeGuess).toInt()
                    progress(p.coerceAtMost(end))
                }
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
