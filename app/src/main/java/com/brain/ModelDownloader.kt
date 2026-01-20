package com.brain

import android.content.Context
import java.io.File
import java.net.URL

class ModelDownloader(val ctx: Context) {

 val base =
 "https://huggingface.co/nvidia/Phi-3.5-mini-Instruct-ONNX-INT4/resolve/main/"

 fun modelDir(): File {
     val f = File(ctx.filesDir, "phi35")
     if(!f.exists()) f.mkdirs()
     return f
 }

 fun isReady(): Boolean {
     return File(modelDir(), "model.onnx.data").length() > 2000000000
 }

 fun downloadAll(p: (Int)->Unit) {

     download("model.onnx")
     p(20)

     // NVIDIA file name
     download("model.onnx_data")
     renameUnderscore()
     p(70)

     download("genai_config.json")
     download("tokenizer.json")
     download("tokenizer_config.json")
     download("special_tokens_map.json")

     p(100)
 }

 private fun download(name: String) {

   val out = File(modelDir(), name)

   if(out.exists() && out.length()>1000) return

   URL(base + name).openStream().use { i ->
      out.outputStream().use { o ->
         i.copyTo(o)
      }
   }
 }

 // 👉 MOST IMPORTANT PART
 private fun renameUnderscore() {

   val old = File(modelDir(), "model.onnx_data")
   val new = File(modelDir(), "model.onnx.data")

   if(old.exists() && !new.exists()) {
       old.renameTo(new)
   }
 }
}
