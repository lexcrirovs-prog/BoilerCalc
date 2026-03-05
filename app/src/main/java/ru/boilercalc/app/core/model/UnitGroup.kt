package ru.boilercalc.app.core.model

data class UnitGroup(
    val name: String,
    val units: List<UnitDef>
)

data class UnitDef(
    val name: String,
    val toBase: (Double) -> Double,
    val fromBase: (Double) -> Double
)
