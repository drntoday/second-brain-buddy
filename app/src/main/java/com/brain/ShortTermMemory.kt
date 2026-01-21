package com.brain

import java.util.concurrent.CopyOnWriteArrayList

class ShortTermMemory(
    private val maxTurns: Int = 50
) {

    private val memory = CopyOnWriteArrayList<String>()

    fun addUser(text: String) {
        add("User: $text")
    }

    fun addAssistant(text: String) {
        add("Solmie: $text")
    }

    private fun add(entry: String) {
        memory.add(entry)

        // Trim old entries
        while (memory.size > maxTurns * 2) {
            memory.removeAt(0)
        }
    }

    fun context(): String {
        if (memory.isEmpty()) return ""

        return memory.joinToString("\n")
    }

    fun clear() {
        memory.clear()
    }
}
