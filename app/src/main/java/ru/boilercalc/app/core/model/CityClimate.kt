package ru.boilercalc.app.core.model

data class CityClimate(
    val name: String,
    val tDesign: Double,
    val tHeating: Double,
    val heatingDays: Int
)
