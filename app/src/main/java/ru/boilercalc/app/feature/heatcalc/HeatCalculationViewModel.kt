package ru.boilercalc.app.feature.heatcalc

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.boilercalc.app.core.data.ClimateDatabase
import ru.boilercalc.app.core.domain.BoilerSelectionEngine
import ru.boilercalc.app.core.domain.HeatCalculationEngine
import ru.boilercalc.app.core.model.Building
import ru.boilercalc.app.core.model.CityClimate
import ru.boilercalc.app.core.util.CyrillicNormalization

class HeatCalculationViewModel : ViewModel() {

    private val _state = MutableStateFlow(HeatCalculationState())
    val state: StateFlow<HeatCalculationState> = _state.asStateFlow()

    private val allCities: List<CityClimate> = ClimateDatabase.cities

    fun onCityQueryChange(text: String) {
        val filtered = if (text.isBlank()) {
            allCities
        } else {
            allCities.filter { CyrillicNormalization.matches(text, it.name) }
        }
        _state.update {
            it.copy(
                cityQuery = text,
                filteredCities = filtered,
                isCityDropdownExpanded = filtered.isNotEmpty(),
                selectedCity = null,
                isCalculated = false
            )
        }
    }

    fun selectCity(city: CityClimate) {
        _state.update {
            it.copy(
                selectedCity = city,
                cityQuery = city.name,
                isCityDropdownExpanded = false,
                heatingDays = city.heatingDays,
                isCalculated = false
            )
        }
    }

    fun dismissCityDropdown() {
        _state.update { it.copy(isCityDropdownExpanded = false) }
    }

    fun addBuilding() {
        _state.update { current ->
            val nextId = (current.buildings.maxOfOrNull { it.id } ?: 0) + 1
            current.copy(
                buildings = current.buildings + Building(id = nextId),
                isCalculated = false
            )
        }
    }

    fun removeBuilding(id: Int) {
        _state.update { current ->
            if (current.buildings.size <= 1) return@update current
            val updated = current.buildings
                .filter { it.id != id }
                .mapIndexed { index, building -> building.copy(id = index + 1) }
            current.copy(buildings = updated, isCalculated = false)
        }
    }

    fun updateBuilding(id: Int, building: Building) {
        _state.update { current ->
            val updated = current.buildings.map {
                if (it.id == id) building else it
            }
            current.copy(buildings = updated, isCalculated = false)
        }
    }

    fun onShowersChange(value: Int) {
        _state.update { it.copy(showers = value.coerceAtLeast(0), isCalculated = false) }
    }

    fun onSinksChange(value: Int) {
        _state.update { it.copy(sinks = value.coerceAtLeast(0), isCalculated = false) }
    }

    fun calculate() {
        val current = _state.value
        val city = current.selectedCity ?: return

        // Calculate volume and heating load for each building
        var totalHeatingLoad = 0.0
        for (building in current.buildings) {
            val volume = if (building.useManualVolume) {
                building.volume
            } else {
                building.width * building.length * building.height
            }
            if (volume > 0) {
                totalHeatingLoad += HeatCalculationEngine.calcHeatingLoad(
                    q0 = building.q0,
                    volume = volume,
                    tDesign = city.tDesign
                )
            }
        }

        val qDHW = HeatCalculationEngine.calcDHWLoad(
            showers = current.showers,
            sinks = current.sinks
        )

        val qTotal = totalHeatingLoad + qDHW

        val annualHeat = HeatCalculationEngine.calcAnnualHeat(
            qHeating = totalHeatingLoad,
            heatingDays = city.heatingDays,
            tHeating = city.tHeating,
            tDesign = city.tDesign
        )

        val gsop = HeatCalculationEngine.calcGSOP(
            tHeating = city.tHeating,
            heatingDays = city.heatingDays
        )

        val selectedBoilers = BoilerSelectionEngine.selectWaterBoilers(qTotal)

        _state.update {
            it.copy(
                qHeating = totalHeatingLoad,
                qDHW = qDHW,
                qTotal = qTotal,
                annualHeat = annualHeat,
                gsop = gsop,
                selectedBoilers = selectedBoilers,
                isCalculated = true
            )
        }
    }
}
