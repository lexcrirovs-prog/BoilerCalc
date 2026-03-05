package ru.boilercalc.app.feature.converter

data class UnitConverterState(
    val selectedGroupIndex: Int = 3,  // Default to extended pressure
    val fromUnitIndex: Int = 0,
    val toUnitIndex: Int = 1,
    val inputText: String = "1",
    val result: Double = 0.0,
    val resultText: String = ""
)
