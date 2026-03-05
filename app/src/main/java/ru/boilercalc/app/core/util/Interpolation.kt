package ru.boilercalc.app.core.util

object Interpolation {
    fun lerp(x: Double, x0: Double, y0: Double, x1: Double, y1: Double): Double {
        if (x1 == x0) return y0
        return y0 + (x - x0) * (y1 - y0) / (x1 - x0)
    }

    fun <T> interpolateFromTable(
        table: List<T>,
        keySelector: (T) -> Double,
        valueSelector: (T) -> Double,
        targetKey: Double
    ): Double {
        if (table.isEmpty()) return 0.0
        val firstKey = keySelector(table.first())
        val lastKey = keySelector(table.last())
        if (targetKey <= firstKey) return valueSelector(table.first())
        if (targetKey >= lastKey) return valueSelector(table.last())

        for (i in 0 until table.size - 1) {
            val k0 = keySelector(table[i])
            val k1 = keySelector(table[i + 1])
            if (targetKey in k0..k1) {
                return lerp(
                    targetKey,
                    k0, valueSelector(table[i]),
                    k1, valueSelector(table[i + 1])
                )
            }
        }
        return valueSelector(table.last())
    }
}
