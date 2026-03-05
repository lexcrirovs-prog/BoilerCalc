package ru.boilercalc.app.core.domain

object HeatCalculationEngine {

    /** Design indoor temperature per ГОСТ 30494-2011, °C */
    private const val T_VN = 20.0

    /** Heat load per shower unit, kW (СП 30.13330) */
    private const val Q_DUSH = 8.0

    /** Heat load per sink, kW (СП 30.13330) */
    private const val Q_UMYV = 1.2

    /**
     * Heating load formula Д.1 (СП 50.13330.2024)
     * Q_от = q₀ × V × (T_VN − t_нр) / 1000 [kW]
     *
     * @param q0 Specific heat loss coefficient, W/(m³·°C)
     * @param volume Building volume, m³
     * @param tDesign Design outdoor temperature, °C
     * @return Heating load, kW
     */
    fun calcHeatingLoad(q0: Double, volume: Double, tDesign: Double): Double {
        return q0 * volume * (T_VN - tDesign) / 1000.0
    }

    /**
     * DHW (domestic hot water) load calculation (СП 30.13330)
     * Q_гвс = n_д × 8 + n_у × 1.2 [kW]
     *
     * @param showers Number of shower units
     * @param sinks Number of sink units
     * @return DHW load, kW
     */
    fun calcDHWLoad(showers: Int, sinks: Int): Double {
        return showers * Q_DUSH + sinks * Q_UMYV
    }

    /**
     * Annual heating energy formula Б.12 (СП 50.13330.2024)
     * Q_год = Q_от × 24 × z_от × (T_VN − t_от) / ((T_VN − t_нр) × 1000) [MWh/year]
     *
     * @param qHeating Heating load at design conditions, kW
     * @param heatingDays Duration of heating period, days
     * @param tHeating Mean heating period temperature, °C
     * @param tDesign Design outdoor temperature, °C
     * @return Annual heat consumption, MWh/year
     */
    fun calcAnnualHeat(qHeating: Double, heatingDays: Int, tHeating: Double, tDesign: Double): Double {
        val denominator = T_VN - tDesign
        if (denominator == 0.0) return 0.0
        return qHeating * 24.0 * heatingDays * (T_VN - tHeating) / (denominator * 1000.0)
    }

    /**
     * Heating degree-days ГСОП (formula 5.2 СП 50.13330.2024)
     * ГСОП = (T_VN − t_от) × z_от [°C·day]
     *
     * @param tHeating Mean heating period temperature, °C
     * @param heatingDays Duration of heating period, days
     * @return GSOP value, °C·day
     */
    fun calcGSOP(tHeating: Double, heatingDays: Int): Double {
        return (T_VN - tHeating) * heatingDays
    }

    /** T_VN constant for external access */
    fun getDesignIndoorTemp(): Double = T_VN
}
