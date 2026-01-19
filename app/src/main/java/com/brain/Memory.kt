package com.brain

import java.io.File

class Memory {

    private val file = File("/data/data/com.brain/solmie_memory.txt")

    // Save conversation
    fun save(text: String) {

        try {
            file.appendText(text + "\n")
        } catch(e: Exception) {}
    }

    // Read last memories
    fun recall(): String {

        return try {
            if(!file.exists()) return ""

            val lines = file.readLines()

            // last 5 memories
            lines.takeLast(5).joinToString("\n")

        } catch(e: Exception) {
            ""
        }
    }

    // Search inside memory
    fun find(keyword: String): String {

        return try {

            val lines = file.readLines()

            lines.filter {
                it.contains(keyword, true)
            }.takeLast(3).joinToString("\n")

        } catch(e: Exception) {
            ""
        }
    }
}
