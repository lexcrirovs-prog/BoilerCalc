package ru.boilercalc.app.feature.converter

data class UnitConverterState(
    val selectedGroupIndex: Int = 0,  // Default to pressure
    val fromUnitIndex: Int = 0,
    val toUnitIndex: Int = 1,
    val inputText: String = "1",
    val result: Double = 0.0,
    val resultText: String = ""
)
