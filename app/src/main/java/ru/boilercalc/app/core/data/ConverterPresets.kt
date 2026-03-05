package ru.boilercalc.app.core.data

object ConverterPresets {
    // Map: group index -> list of (fromUnit, toUnit) pairs
    val presets: Map<Int, List<Pair<String, String>>> = mapOf(
        3 to listOf(  // Давление расш.
            "бар" to "кгс/см²",
            "МПа" to "атм",
            "кПа" to "мбар",
            "бар" to "psi"
        ),
        4 to listOf(  // Тепловая мощность
            "МВт" to "Гкал/ч",
            "кВт" to "ккал/ч",
            "кВт" to "BTU/h"
        ),
        5 to listOf(  // Температура полн.
            "°C" to "K",
            "°C" to "°F"
        ),
        6 to listOf(  // Расход пара
            "т/ч" to "МВт",
            "кг/ч" to "кВт",
            "т/ч" to "кг/с",
            "кг/ч" to "т/сут"
        ),
        7 to listOf(  // Энтальпия
            "кДж/кг" to "ккал/кг",
            "кДж/кг" to "BTU/lb"
        ),
        8 to listOf(  // Удельный объём
            "м³/кг" to "л/кг",
            "м³/кг" to "ft³/lb"
        ),
        9 to listOf( // Расход топлива
            "м³/ч" to "тыс.м³/сут",
            "нм³/ч" to "кг/ч"
        ),
        10 to listOf( // Теплотворность
            "МДж/м³" to "ккал/м³",
            "МДж/м³" to "кВт·ч/м³"
        ),
        11 to listOf( // Объёмный расход
            "м³/ч" to "л/с",
            "л/мин" to "GPM"
        )
    )
}
