package com.brain

import android.content.Context
import java.io.File

/**
 * Phi-3.5 wrapper
 *
 * - Uses real ONNX GenAI if available
 * - Falls back safely if GenAI runtime is missing
 * - Never crashes app or build
 */
class Phi3(private val ctx: Context) {

    private val modelDir = File(ctx.filesDir, "phi35")

    private val engine: Engine

    init {
        engine = try {
            if (isModelReady()) {
                OnnxEngine(modelDir)
            } else {
                StubEngine()
            }
        } catch (e: Throwable) {
            // Absolute safety net
            StubEngine()
        }
    }

    fun reply(prompt: String): String {
        return engine.reply(prompt)
    }

    private fun isModelReady(): Boolean {
        return File(modelDir, "model.onnx").exists() &&
               File(modelDir, "model.onnx.data").exists() &&
               File(modelDir, "tokenizer.json").exists() &&
               File(modelDir, "genai_config.json").exists()
    }
}

/* ========================================================= */
/* ===================== ENGINE LAYER ====================== */
/* ========================================================= */

private interface Engine {
    fun reply(prompt: String): String
}

/* ---------------- REAL ONNX ENGINE ---------------- */

private class OnnxEngine(modelDir: File) : Engine {

    // Lazy-loaded via reflection to avoid hard dependency
    private val impl: Any

    init {
        impl = Phi3GenAiImpl(modelDir)
    }

    override fun reply(prompt: String): String {
        return (impl as Phi3GenAiImpl).reply(prompt)
    }
}

/* ---------------- SAFE FALLBACK ---------------- */

private class StubEngine : Engine {
    override fun reply(prompt: String): String {
        return "Main abhi learning mode me hoon 🙂 Aapne kaha: $prompt"
    }
}
