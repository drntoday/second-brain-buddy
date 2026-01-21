package com.brain

object TextChunker {

    fun chunk(text: String): List<String> {
        // Split by sentence first
        val sentences = text
            .replace("\n", " ")
            .split(Regex("(?<=[.!?])\\s+"))

        val chunks = mutableListOf<String>()

        var buffer = StringBuilder()

        for (s in sentences) {
            if (buffer.length + s.length < 120) {
                buffer.append(s).append(" ")
            } else {
                chunks.add(buffer.toString().trim())
                buffer = StringBuilder(s).append(" ")
            }
        }

        if (buffer.isNotEmpty()) {
            chunks.add(buffer.toString().trim())
        }

        return chunks.filter { it.isNotBlank() }
    }
}
