package com.brain

import java.net.URL
import org.json.JSONArray
import org.json.JSONObject

class Search {

    // --- MAIN ENTRY ---
    fun web(query: String): String {

        return try {

            // 1. Try DuckDuckGo first (free)
            val ddg = duck(query)
            if(ddg.isNotEmpty()) return ddg

            // 2. Try Wikipedia
            val wiki = wiki(query)
            if(wiki.isNotEmpty()) return wiki

            // 3. Fallback to Bing (later you will add key)
            val bing = bing(query)
            if(bing.isNotEmpty()) return bing

            "Dost, abhi exact info nahi mili. Thoda alag tarike se pucho na."

        } catch(e: Exception) {
            "Net me dikkat lag rahi hai yaar."
        }
    }

    // --- DUCKDUCKGO ---
    fun duck(q: String): String {

        val url =
        "https://api.duckduckgo.com/?q=" +
        q.replace(" ","+") +
        "&format=json&no_html=1"

        val text = URL(url).readText()

        val json = JSONObject(text)
        val abs = json.optString("AbstractText")

        return if(abs.length > 10)
            "Mila ye: $abs"
        else ""
    }

    // --- WIKIPEDIA ---
    fun wiki(q: String): String {

        val url =
        "https://en.wikipedia.org/api/rest_v1/page/summary/" +
        q.replace(" ","_")

        val text = URL(url).readText()

        val json = JSONObject(text)

        return json.optString("extract")
    }

    // --- BING PLACEHOLDER ---
    fun bing(q: String): String {

        // Yahan aap baad me Bing API key daloge
        return ""
    }
}
