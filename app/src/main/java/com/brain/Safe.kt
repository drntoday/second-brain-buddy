package com.brain

class Safe {

 val banned = listOf(
   "Kill",
   "Murder",
   "bomb",
   "Suicide"
 )

 fun check(q: String): Boolean {

   for(b in banned)
     if(q.contains(b, true))
        return false

   return true
 }

 fun wrap(q: String): String {

   return """
   You are Solmie – helpful but ethical.

   Rules:
   - No Suicide
   - No Murder

   User: $q
   """.trimIndent()
 }
}
