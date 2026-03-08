package ru.boilercalc.app.feature.economics

import ru.boilercalc.app.core.model.EconBoilerModel
import ru.boilercalc.app.core.model.PaybackResult

data class EconomicsState(
    // Boiler selection
    val boilerCategory: String = "steam", // steam | water | waterE
    val selectedModel: EconBoilerModel? = null,
    val boilerCount: Int = 1,
    val useEconomizer: Boolean = false,

    // OPEX params
    val loadPercent: Double = 0.75,
    val loadText: String = "75",
    val dailyHours: Double = 24.0,
    val dailyHoursText: String = "24",
    val workDays: Int = 350,
    val workDaysText: String = "350",
    val gasPrice: Double = 8.0,
    val gasPriceText: String = "8",
    val maintenanceCost: Double = 300000.0,
    val maintenanceCostText: String = "300 000",

    // OPEX results
    val annualGas: Double = 0.0,
    val annualOPEX: Double = 0.0,
    val annualHeatGcal: Double = 0.0,

    // CAPEX
    val capex: Double = 0.0,
    val additionalCapex: Double = 0.0,
    val additionalCapexText: String = "",

    // Tariff
    val tarifGcal: Double = 0.0,
    val tarifGcalText: String = "",

    // Revenue
    val revenueMode: String = "uniform",  // uniform | variable
    val uniformRevenue: Double = 0.0,
    val uniformRevenueText: String = "",
    val variableRevenues: List<Double> = List(10) { 0.0 },
    val yearsCount: Int = 10,
    val discountRate: Double = 0.10,
    val discountRateText: String = "10",

    // Results
    val paybackResult: PaybackResult? = null,
    val isCalculated: Boolean = false
)
