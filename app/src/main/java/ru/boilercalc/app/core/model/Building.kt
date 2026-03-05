package ru.boilercalc.app.core.model

data class Building(
    val id: Int = 0,
    val type: String = "Производственное",
    val width: Double = 0.0,
    val length: Double = 0.0,
    val height: Double = 0.0,
    val volume: Double = 0.0,
    val q0: Double = 0.5,
    val useManualVolume: Boolean = false
)
