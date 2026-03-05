package ru.boilercalc.app.core.util

object CyrillicNormalization {
    fun normalize(text: String): String {
        return text.lowercase()
            .replace('ё', 'е')
            .replace('Ё', 'Е')
            .trim()
    }

    fun matches(query: String, target: String): Boolean {
        if (query.isBlank()) return true
        return normalize(target).contains(normalize(query))
    }
}
