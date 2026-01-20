package com.brain

import android.content.Context
import java.io.File

class ModelDownloader(val ctx: Context) {

    fun isModelPresent(): Boolean {
        val f = File(ctx.filesDir, "phi35/model.onnx")
        return f.exists()
    }

    fun getPath(): String {
        return ctx.filesDir.absolutePath + "/phi35"
    }
}
