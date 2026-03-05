package ru.boilercalc.app.feature.steam

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.boilercalc.app.core.domain.BoilerSelectionEngine
import ru.boilercalc.app.core.domain.SteamCalculationEngine
import ru.boilercalc.app.core.util.Formatting

class SteamPropertiesViewModel : ViewModel() {

    private val _state = MutableStateFlow(SteamPropertiesState())
    val state: StateFlow<SteamPropertiesState> = _state.asStateFlow()

    init {
        calculate()
    }

    // --- Pressure change: recalculate temperature from pressure ---
    fun onPressureChange(text: String) {
        val s = _state.value
        if (s.isUpdatingFromTemp) return

        val parsed = text.replace(',', '.').toDoubleOrNull()
        val pressureBar = parsed?.coerceIn(0.0, 16.0) ?: s.pressureBar

        val props = SteamCalculationEngine.getSteamProperties(pressureBar)
        _state.value = s.copy(
            pressureText = text,
            pressureBar = pressureBar,
            temperatureC = props.temperature,
            temperatureText = Formatting.formatNumber(props.temperature, 1)
                .trimEnd('0').trimEnd(',')
        )
        calculate()
    }

    // --- Temperature change: recalculate pressure from temperature ---
    fun onTemperatureChange(text: String) {
        val s = _state.value
        val parsed = text.replace(',', '.').toDoubleOrNull()
        val tempC = parsed?.coerceIn(99.0, 204.3) ?: s.temperatureC

        _state.value = s.copy(isUpdatingFromTemp = true)

        val pressureBar = SteamCalculationEngine.getPressureFromTemp(tempC)
        _state.value = _state.value.copy(
            temperatureText = text,
            temperatureC = tempC,
            pressureBar = pressureBar,
            pressureText = Formatting.formatNumber(pressureBar, 2)
                .trimEnd('0').trimEnd(','),
            isUpdatingFromTemp = false
        )
        calculate()
    }

    fun onPressureSliderChange(value: Float) {
        val pressureBar = value.toDouble()
        val props = SteamCalculationEngine.getSteamProperties(pressureBar)
        _state.value = _state.value.copy(
            pressureBar = pressureBar,
            pressureText = Formatting.formatNumber(pressureBar, 2)
                .trimEnd('0').trimEnd(','),
            temperatureC = props.temperature,
            temperatureText = Formatting.formatNumber(props.temperature, 1)
                .trimEnd('0').trimEnd(',')
        )
        calculate()
    }

    fun onTemperatureSliderChange(value: Float) {
        val tempC = value.toDouble()
        val pressureBar = SteamCalculationEngine.getPressureFromTemp(tempC)
        _state.value = _state.value.copy(
            temperatureC = tempC,
            temperatureText = Formatting.formatNumber(tempC, 1)
                .trimEnd('0').trimEnd(','),
            pressureBar = pressureBar,
            pressureText = Formatting.formatNumber(pressureBar, 2)
                .trimEnd('0').trimEnd(',')
        )
        calculate()
    }

    fun onSteamCapacityChange(text: String) {
        val parsed = text.replace(',', '.').toDoubleOrNull()
        _state.value = _state.value.copy(
            steamCapacityText = text,
            steamCapacityKgH = parsed?.coerceIn(0.0, 15000.0) ?: _state.value.steamCapacityKgH
        )
        calculate()
    }

    fun onSteamCapacitySliderChange(value: Float) {
        val capacity = value.toDouble()
        _state.value = _state.value.copy(
            steamCapacityKgH = capacity,
            steamCapacityText = Formatting.formatNumber(capacity, 0)
        )
        calculate()
    }

    fun onCalorificChange(text: String) {
        val parsed = text.replace(',', '.').toDoubleOrNull()
        _state.value = _state.value.copy(
            calorificText = text,
            calorificKcal = parsed ?: _state.value.calorificKcal
        )
        calculate()
    }

    fun onEfficiencyChange(text: String) {
        val parsed = text.replace(',', '.').toDoubleOrNull()
        _state.value = _state.value.copy(
            efficiencyText = text,
            efficiencyPercent = parsed?.coerceIn(50.0, 100.0) ?: _state.value.efficiencyPercent
        )
        calculate()
    }

    // --- Unit toggles ---
    fun togglePressureUnit() {
        val current = _state.value.pressureUnit
        val next = when (current) {
            "бар" -> "МПа"
            "МПа" -> "кгс/см²"
            else -> "бар"
        }
        _state.value = _state.value.copy(pressureUnit = next)
    }

    fun toggleTempUnit() {
        val current = _state.value.tempUnit
        val next = when (current) {
            "\u00B0C" -> "\u00B0F"
            else -> "\u00B0C"
        }
        _state.value = _state.value.copy(tempUnit = next)
    }

    fun toggleCapacityUnit() {
        val current = _state.value.capacityUnit
        val next = when (current) {
            "кг/ч" -> "т/ч"
            else -> "кг/ч"
        }
        _state.value = _state.value.copy(capacityUnit = next)
    }

    fun togglePowerUnit() {
        val current = _state.value.powerUnit
        val next = when (current) {
            "МВт" -> "Гкал/ч"
            "Гкал/ч" -> "кВт"
            else -> "МВт"
        }
        _state.value = _state.value.copy(powerUnit = next)
    }

    // --- Display value converters ---
    fun displayPressure(): String {
        val s = _state.value
        val barValue = s.pressureBar
        return when (s.pressureUnit) {
            "МПа" -> Formatting.formatNumber(barValue * 0.1, 4).trimEnd('0').trimEnd(',')
            "кгс/см²" -> Formatting.formatNumber(barValue * 1.01972, 3).trimEnd('0').trimEnd(',')
            else -> Formatting.formatNumber(barValue, 2).trimEnd('0').trimEnd(',')
        }
    }

    fun displayTemperature(): String {
        val s = _state.value
        return when (s.tempUnit) {
            "\u00B0F" -> Formatting.formatNumber(s.temperatureC * 9.0 / 5.0 + 32.0, 1).trimEnd('0').trimEnd(',')
            else -> Formatting.formatNumber(s.temperatureC, 1).trimEnd('0').trimEnd(',')
        }
    }

    fun displayCapacity(): String {
        val s = _state.value
        return when (s.capacityUnit) {
            "т/ч" -> Formatting.formatNumber(s.steamCapacityKgH / 1000.0, 3).trimEnd('0').trimEnd(',')
            else -> Formatting.formatNumber(s.steamCapacityKgH, 0)
        }
    }

    fun displayPower(): String {
        val s = _state.value
        return when (s.powerUnit) {
            "Гкал/ч" -> Formatting.formatNumber(s.powerMW * 1.163, 4).trimEnd('0').trimEnd(',')
            "кВт" -> Formatting.formatNumber(s.powerMW * 1000.0, 1).trimEnd('0').trimEnd(',')
            else -> Formatting.formatNumber(s.powerMW, 4).trimEnd('0').trimEnd(',')
        }
    }

    // --- Main calculation ---
    private fun calculate() {
        val s = _state.value
        val props = SteamCalculationEngine.getSteamProperties(s.pressureBar)
        val gasConsumption = SteamCalculationEngine.calcGasConsumption(
            steamKgH = s.steamCapacityKgH,
            hDoublePrime = props.hDoublePrime,
            calorificKcal = s.calorificKcal,
            efficiency = s.efficiencyPercent / 100.0
        )
        val powerMW = SteamCalculationEngine.calcPowerMW(
            steamKgH = s.steamCapacityKgH,
            latentHeat = props.latentHeat
        )
        val boilers = BoilerSelectionEngine.selectSteamBoilers(s.steamCapacityKgH)

        _state.value = _state.value.copy(
            steamProps = props,
            gasConsumption = gasConsumption,
            powerMW = powerMW,
            selectedBoilers = boilers
        )
    }
}
