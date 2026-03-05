package ru.boilercalc.app.core.model

data class EconomicsResult(
    val year: Int,
    val revenue: Double,
    val cashFlow: Double,
    val cumCashFlow: Double,
    val presentValue: Double,
    val cumPresentValue: Double
)

data class PaybackResult(
    val pp: Double,
    val dpbp: Double,
    val table: List<EconomicsResult>
)
