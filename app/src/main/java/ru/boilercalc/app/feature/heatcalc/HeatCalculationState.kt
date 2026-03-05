package ru.boilercalc.app.feature.heatcalc

import ru.boilercalc.app.core.domain.SelectedWaterBoiler
import ru.boilercalc.app.core.model.Building
import ru.boilercalc.app.core.model.CityClimate

data class HeatCalculationState(
    // City
    val cityQuery: String = "",
    val selectedCity: CityClimate? = null,
    val filteredCities: List<CityClimate> = emptyList(),
    val isCityDropdownExpanded: Boolean = false,

    // Buildings
    val buildings: List<Building> = listOf(Building(id = 1)),

    // DHW
    val showers: Int = 0,
    val sinks: Int = 0,

    // Results
    val qHeating: Double = 0.0,
    val qDHW: Double = 0.0,
    val qTotal: Double = 0.0,
    val annualHeat: Double = 0.0,
    val gsop: Double = 0.0,
    val selectedBoilers: List<SelectedWaterBoiler> = emptyList(),
    val isCalculated: Boolean = false,

    // For cross-tab sharing with Economics
    val heatingDays: Int = 0
)
