package ru.boilercalc.app.core.model

data class EconBoilerModel(
    val id: String,
    val name: String,
    val maxGas: Double,
    val price: Long,
    val efficiency: Double,
    val economizerPrice: Long? = null
)
