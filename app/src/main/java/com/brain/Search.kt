package com.brain

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class Search {

    /* ---------------- PUBLIC API ---------------- */

    fun wiki(query: String): String {
        return try {
            val url = URL(
                "https://en.wikipedia.org/api/rest_v1/page/summary/" +
                        query.trim().replace(" ", "_")
            )

            val conn = open(url)
            val json = JSONObject(conn.inputStream.bufferedReader().readText())

            val title = json.optString("title")
            val extract = json.optString("extract")

            if (extract.isBlank()) "" else {
                "Title: $title\nSummary: $extract"
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun web(query: String): String {
        return try {
            val url = URL(
                "https://api.duckduckgo.com/?" +
                        "q=${query.trim().replace(" ", "+")}" +
                        "&format=json&no_redirect=1&no_html=1"
            )

            val conn = open(url)
            val json = JSONObject(conn.inputStream.bufferedReader().readText())

            val abstract = json.optString("AbstractText")
            val heading = json.optString("Heading")

            if (abstract.isBlank()) "" else {
                "Topic: $heading\nInfo: $abstract"
            }
        } catch (e: Exception) {
            ""
        }
    }

    /* ---------------- INTERNAL ---------------- */

    private fun open(url: URL): HttpURLConnection {
        return (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 4_000
            readTimeout = 4_000
            requestMethod = "GET"
            setRequestProperty(
                "User-Agent",
                "Solmie/1.0 (Android Assistant)"
            )
        }
    }
}
