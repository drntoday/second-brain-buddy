package com.brain

import java.net.URL

class Search {

 fun web(q: String): String {

   return try {

     val url =
      "https://api.duckduckgo.com/?q=" +
       q.replace(" ","+") +
      "&format=json"

     URL(url).readText()

   } catch(e: Exception){
     ""
   }
 }

 fun wiki(q: String): String {

   return try {

     val url =
      "https://en.wikipedia.org/api/rest_v1/page/summary/" +
       q.replace(" ","_")

     URL(url).readText()

   } catch(e: Exception){
     ""
   }
 }
}
