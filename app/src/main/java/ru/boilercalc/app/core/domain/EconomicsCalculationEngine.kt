package ru.boilercalc.app.core.domain

import ru.boilercalc.app.core.model.EconomicsResult
import ru.boilercalc.app.core.model.PaybackResult
import kotlin.math.pow

object EconomicsCalculationEngine {

    /**
     * Calculate annual OPEX (operating expenses).
     *
     * @param maxGas Maximum gas consumption, m³/h
     * @param loadPercent Load factor (0..1)
     * @param dailyHours Hours of operation per day
     * @param workDays Working days per year
     * @param gasPrice Gas price, rubles per m³
     * @param maintenanceCost Annual maintenance cost, rubles
     * @param boilerCount Number of boilers
     * @param efficiency Boiler efficiency (0..1)
     * @return Annual OPEX, rubles
     */
    fun calcOPEX(
        maxGas: Double,
        loadPercent: Double,
        dailyHours: Double,
        workDays: Int,
        gasPrice: Double,
        maintenanceCost: Double,
        boilerCount: Int = 1,
        efficiency: Double = 0.92
    ): Double {
        val annualGas = maxGas * loadPercent * dailyHours * workDays * boilerCount
        val fuelCost = annualGas * gasPrice
        return fuelCost + maintenanceCost
    }

    /**
     * Calculate annual gas consumption.
     */
    fun calcAnnualGas(
        maxGas: Double,
        loadPercent: Double,
        dailyHours: Double,
        workDays: Int,
        boilerCount: Int = 1
    ): Double {
        return maxGas * loadPercent * dailyHours * workDays * boilerCount
    }

    /**
     * Calculate annual heat output in Gcal.
     * Uses natural gas calorific value 8484 kcal/m³ and boiler efficiency.
     *
     * @param annualGas Annual gas consumption, m³
     * @param efficiency Boiler efficiency (0..1)
     * @return Annual heat output, Gcal
     */
    fun calcAnnualHeatGcal(
        annualGas: Double,
        efficiency: Double
    ): Double {
        return annualGas * efficiency * 8484.0 / 1_000_000.0
    }

    /**
     * Calculate PP (simple payback) and DPBP (discounted payback).
     *
     * @param capex Initial investment (K₀), rubles
     * @param revenues List of annual revenues (can be variable length, up to 30 years)
     * @param opex Annual operating expenses, rubles
     * @param discountRate Discount rate (e.g. 0.10 for 10%)
     * @return PaybackResult with PP, DPBP and year-by-year table
     */
    fun calcPayback(
        capex: Double,
        revenues: List<Double>,
        opex: Double,
        discountRate: Double
    ): PaybackResult {
        val table = mutableListOf<EconomicsResult>()
        var cumCF = -capex
        var cumPV = -capex
        var pp = -1.0
        var dpbp = -1.0

        for (i in revenues.indices) {
            val year = i + 1
            val revenue = revenues[i]
            val cf = revenue - opex
            val prevCumCF = cumCF
            cumCF += cf

            val pv = cf / (1.0 + discountRate).pow(year.toDouble())
            val prevCumPV = cumPV
            cumPV += pv

            table.add(
                EconomicsResult(
                    year = year,
                    revenue = revenue,
                    cashFlow = cf,
                    cumCashFlow = cumCF,
                    presentValue = pv,
                    cumPresentValue = cumPV
                )
            )

            // PP: fractional interpolation
            if (pp < 0 && cumCF >= 0 && prevCumCF < 0) {
                pp = year - 1 + (-prevCumCF) / cf
            }

            // DPBP: fractional interpolation
            if (dpbp < 0 && cumPV >= 0 && prevCumPV < 0) {
                dpbp = year - 1 + (-prevCumPV) / pv
            }
        }

        return PaybackResult(
            pp = pp,
            dpbp = dpbp,
            table = table
        )
    }
}
