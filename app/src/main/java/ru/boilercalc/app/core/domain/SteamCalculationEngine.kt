package ru.boilercalc.app.core.domain

import ru.boilercalc.app.core.data.SteamTable
import ru.boilercalc.app.core.model.SteamDataPoint
import ru.boilercalc.app.core.util.Interpolation

data class SteamProperties(
    val temperature: Double,
    val hPrime: Double,
    val hDoublePrime: Double,
    val latentHeat: Double,
    val specificVolume: Double
)

object SteamCalculationEngine {

    private val table = SteamTable.entries

    /** Feedwater enthalpy at ~60°C (kJ/kg) */
    private const val H_FEEDWATER = 251.0

    /** Calorific value conversion: kcal → kJ */
    private const val KCAL_TO_KJ = 4.1868

    /**
     * Get steam properties by gauge pressure (0..16 bar).
     * Uses linear interpolation on IAPWS-IF97 table.
     */
    fun getSteamProperties(pressureGauge: Double): SteamProperties {
        val p = pressureGauge.coerceIn(0.0, 16.0)
        val t = Interpolation.interpolateFromTable(table, { it.pressureGauge }, { it.temperature }, p)
        val hP = Interpolation.interpolateFromTable(table, { it.pressureGauge }, { it.hPrime }, p)
        val hPP = Interpolation.interpolateFromTable(table, { it.pressureGauge }, { it.hDoublePrime }, p)
        val v = Interpolation.interpolateFromTable(table, { it.pressureGauge }, { it.specificVolume }, p)
        return SteamProperties(
            temperature = t,
            hPrime = hP,
            hDoublePrime = hPP,
            latentHeat = hPP - hP,
            specificVolume = v
        )
    }

    /**
     * Reverse lookup: temperature → gauge pressure.
     * Uses linear interpolation on the temperature column.
     */
    fun getPressureFromTemp(tempC: Double): Double {
        val t = tempC.coerceIn(table.first().temperature, table.last().temperature)
        return Interpolation.interpolateFromTable(table, { it.temperature }, { it.pressureGauge }, t)
    }

    /**
     * Gas consumption calculation.
     * @param steamKgH Steam flow rate, kg/h
     * @param hDoublePrime Enthalpy of steam, kJ/kg
     * @param calorificKcal Fuel calorific value, kcal/m³ (default 8484)
     * @param efficiency Boiler efficiency (default 0.92)
     * @return Gas consumption, m³/h
     */
    fun calcGasConsumption(
        steamKgH: Double,
        hDoublePrime: Double,
        calorificKcal: Double = 8484.0,
        efficiency: Double = 0.92
    ): Double {
        val qNeeded = steamKgH * (hDoublePrime - H_FEEDWATER)
        val qFuel = calorificKcal * KCAL_TO_KJ
        return if (qFuel * efficiency > 0) qNeeded / (qFuel * efficiency) else 0.0
    }

    /**
     * Calculate thermal power from steam flow.
     * Q = D * r / 3_600_000 (MW)
     * @param steamKgH Steam flow rate, kg/h
     * @param latentHeat Latent heat r, kJ/kg
     * @return Power in MW
     */
    fun calcPowerMW(steamKgH: Double, latentHeat: Double): Double {
        return steamKgH * latentHeat / 3_600_000.0
    }
}
