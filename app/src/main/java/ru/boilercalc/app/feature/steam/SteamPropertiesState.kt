package ru.boilercalc.app.feature.steam

import ru.boilercalc.app.core.domain.SelectedSteamBoiler
import ru.boilercalc.app.core.domain.SteamProperties

data class SteamPropertiesState(
    // Inputs
    val pressureBar: Double = 8.0,
    val pressureText: String = "8",
    val temperatureC: Double = 175.4,
    val temperatureText: String = "175.4",
    val steamCapacityKgH: Double = 2000.0,
    val steamCapacityText: String = "2000",
    val calorificKcal: Double = 8484.0,
    val calorificText: String = "8484",
    val efficiencyPercent: Double = 92.0,
    val efficiencyText: String = "92",

    // Results
    val steamProps: SteamProperties? = null,
    val powerMW: Double = 0.0,
    val gasConsumption: Double = 0.0,

    // Selected boilers
    val selectedBoilers: List<SelectedSteamBoiler> = emptyList(),

    // Unit display modes
    val pressureUnit: String = "бар",       // бар | МПа | кгс/см²
    val tempUnit: String = "\u00B0C",       // °C | °F
    val capacityUnit: String = "кг/ч",      // кг/ч | т/ч
    val powerUnit: String = "МВт",          // МВт | Гкал/ч | кВт
    val gasUnit: String = "м\u00B3/ч",      // м³/ч

    // Sync flag to prevent loops
    val isUpdatingFromTemp: Boolean = false
)
