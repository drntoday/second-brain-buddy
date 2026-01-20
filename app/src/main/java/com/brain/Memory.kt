package com.brain

import android.content.Context
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

class Memory(val ctx: Context) {

 val file = File(ctx.filesDir, "memory.json")

 init {
   if(!file.exists())
      file.writeText("[]")
 }

 fun add(q: String, a: String){

   val arr = JSONArray(file.readText())

   val o = JSONObject()
   o.put("q", q)
   o.put("a", a)
   o.put("t", System.currentTimeMillis())

   arr.put(o)

   file.writeText(arr.toString())
 }

 fun recall(query: String): String? {

   val arr = JSONArray(file.readText())

   for(i in arr.length()-1 downTo 0){

     val o = arr.getJSONObject(i)

     val q = o.getString("q")

     if(query.contains(q, true))
        return o.getString("a")
   }

   return null
 }

 fun context(): String {

   val arr = JSONArray(file.readText())

   var s = "Past notes:\n"

   for(i in 0 until arr.length()){

     val o = arr.getJSONObject(i)

     s += "- " + o.getString("q") +
          " → " + o.getString("a") + "\n"
   }

   return s
 }
}
