package ru.boilercalc.app.core.model

data class SteamDataPoint(
    val pressureGauge: Double,
    val temperature: Double,
    val hPrime: Double,
    val hDoublePrime: Double,
    val latentHeat: Double,
    val specificVolume: Double
)
