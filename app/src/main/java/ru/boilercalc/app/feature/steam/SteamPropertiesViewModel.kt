package ru.boilercalc.app.feature.steam

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.boilercalc.app.core.domain.BoilerSelectionEngine
import ru.boilercalc.app.core.domain.SteamCalculationEngine
import ru.boilercalc.app.core.domain.UnitConversionEngine
import ru.boilercalc.app.core.util.Formatting

class SteamPropertiesViewModel : ViewModel() {

    private val _state = MutableStateFlow(SteamPropertiesState())
    val state: StateFlow<SteamPropertiesState> = _state.asStateFlow()

    init {
        calculate()
    }

    // ═══ Conversion helpers ═══

    private fun barToDisplay(bar: Double, unit: String): Double = when (unit) {
        "МПа" -> bar * 0.1
        "кгс/см²" -> bar * 1.01972
        else -> bar
    }

    private fun displayToBar(value: Double, unit: String): Double = when (unit) {
        "МПа" -> value * 10.0
        "кгс/см²" -> value / 1.01972
        else -> value
    }

    private fun pressureDecimals(unit: String): Int = when (unit) {
        "МПа" -> 4
        "кгс/см²" -> 3
        else -> 2
    }

    private fun celsiusToDisplay(c: Double, unit: String): Double = when (unit) {
        "\u00B0F" -> c * 9.0 / 5.0 + 32.0
        else -> c
    }

    private fun displayToCelsius(value: Double, unit: String): Double = when (unit) {
        "\u00B0F" -> (value - 32.0) * 5.0 / 9.0
        else -> value
    }

    private fun kghToDisplay(kgh: Double, unit: String): Double =
        UnitConversionEngine.convert(3, "кг/ч", unit, kgh)

    private fun displayToKgh(value: Double, unit: String): Double =
        UnitConversionEngine.convert(3, unit, "кг/ч", value)

    private fun capacityDecimals(unit: String): Int = when (unit) {
        "т/ч" -> 3
        "МВт" -> 3
        "Гкал/ч" -> 3
        else -> 0
    }

    private fun formatDisplay(value: Double, decimals: Int): String {
        val formatted = Formatting.formatNumber(value, decimals)
        return if (decimals > 0) formatted.trimEnd('0').trimEnd(',') else formatted
    }

    // ═══ Slider value/range helpers ═══

    fun pressureSliderValue(): Float {
        val s = _state.value
        return barToDisplay(s.pressureBar, s.pressureUnit).toFloat()
    }

    fun pressureSliderRange(): ClosedFloatingPointRange<Float> = when (_state.value.pressureUnit) {
        "МПа" -> 0f..1.6f
        "кгс/см²" -> 0f..16.316f
        else -> 0f..16f
    }

    fun temperatureSliderValue(): Float {
        val s = _state.value
        return celsiusToDisplay(s.temperatureC, s.tempUnit).toFloat()
    }

    fun temperatureSliderRange(): ClosedFloatingPointRange<Float> = when (_state.value.tempUnit) {
        "\u00B0F" -> 210.2f..399.74f
        else -> 99f..204.3f
    }

    fun capacitySliderValue(): Float {
        val s = _state.value
        return kghToDisplay(s.steamCapacityKgH, s.capacityUnit).toFloat()
    }

    fun capacitySliderRange(): ClosedFloatingPointRange<Float> = when (_state.value.capacityUnit) {
        "т/ч" -> 0f..100f
        "МВт" -> 0f..60f
        "Гкал/ч" -> 0f..50f
        else -> 0f..100000f
    }

    // ═══ Pressure change: recalculate temperature from pressure ═══

    fun onPressureChange(text: String) {
        val s = _state.value
        if (s.isUpdatingFromTemp) return

        val parsed = text.replace(',', '.').toDoubleOrNull()
        val pressureBar = if (parsed != null) {
            displayToBar(parsed, s.pressureUnit).coerceIn(0.0, 16.0)
        } else {
            s.pressureBar
        }

        val props = SteamCalculationEngine.getSteamProperties(pressureBar)
        _state.value = s.copy(
            pressureText = text,
            pressureBar = pressureBar,
            temperatureC = props.temperature,
            temperatureText = formatDisplay(
                celsiusToDisplay(props.temperature, s.tempUnit), 1
            )
        )
        calculate()
    }

    // ═══ Temperature change: recalculate pressure from temperature ═══

    fun onTemperatureChange(text: String) {
        val s = _state.value
        val parsed = text.replace(',', '.').toDoubleOrNull()
        val tempC = if (parsed != null) {
            displayToCelsius(parsed, s.tempUnit).coerceIn(99.0, 204.3)
        } else {
            s.temperatureC
        }

        _state.value = s.copy(isUpdatingFromTemp = true)

        val pressureBar = SteamCalculationEngine.getPressureFromTemp(tempC)
        _state.value = _state.value.copy(
            temperatureText = text,
            temperatureC = tempC,
            pressureBar = pressureBar,
            pressureText = formatDisplay(
                barToDisplay(pressureBar, s.pressureUnit), pressureDecimals(s.pressureUnit)
            ),
            isUpdatingFromTemp = false
        )
        calculate()
    }

    fun onPressureSliderChange(value: Float) {
        val s = _state.value
        val pressureBar = displayToBar(value.toDouble(), s.pressureUnit)
        val props = SteamCalculationEngine.getSteamProperties(pressureBar)
        _state.value = s.copy(
            pressureBar = pressureBar,
            pressureText = formatDisplay(value.toDouble(), pressureDecimals(s.pressureUnit)),
            temperatureC = props.temperature,
            temperatureText = formatDisplay(
                celsiusToDisplay(props.temperature, s.tempUnit), 1
            )
        )
        calculate()
    }

    fun onTemperatureSliderChange(value: Float) {
        val s = _state.value
        val tempC = displayToCelsius(value.toDouble(), s.tempUnit)
        val pressureBar = SteamCalculationEngine.getPressureFromTemp(tempC)
        _state.value = s.copy(
            temperatureC = tempC,
            temperatureText = formatDisplay(value.toDouble(), 1),
            pressureBar = pressureBar,
            pressureText = formatDisplay(
                barToDisplay(pressureBar, s.pressureUnit), pressureDecimals(s.pressureUnit)
            )
        )
        calculate()
    }

    fun onSteamCapacityChange(text: String) {
        val s = _state.value
        val parsed = text.replace(',', '.').toDoubleOrNull()
        val kgH = if (parsed != null) {
            displayToKgh(parsed, s.capacityUnit).coerceIn(0.0, 100000.0)
        } else {
            s.steamCapacityKgH
        }
        _state.value = s.copy(
            steamCapacityText = text,
            steamCapacityKgH = kgH
        )
        calculate()
    }

    fun onSteamCapacitySliderChange(value: Float) {
        val s = _state.value
        val kgH = displayToKgh(value.toDouble(), s.capacityUnit)
        _state.value = s.copy(
            steamCapacityKgH = kgH,
            steamCapacityText = formatDisplay(value.toDouble(), capacityDecimals(s.capacityUnit))
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

    // ═══ Unit toggles ═══

    fun togglePressureUnit() {
        val s = _state.value
        val next = when (s.pressureUnit) {
            "бар" -> "МПа"
            "МПа" -> "кгс/см²"
            else -> "бар"
        }
        _state.value = s.copy(
            pressureUnit = next,
            pressureText = formatDisplay(barToDisplay(s.pressureBar, next), pressureDecimals(next))
        )
    }

    fun toggleTempUnit() {
        val s = _state.value
        val next = when (s.tempUnit) {
            "\u00B0C" -> "\u00B0F"
            else -> "\u00B0C"
        }
        _state.value = s.copy(
            tempUnit = next,
            temperatureText = formatDisplay(celsiusToDisplay(s.temperatureC, next), 1)
        )
    }

    fun toggleCapacityUnit() {
        val s = _state.value
        val next = when (s.capacityUnit) {
            "кг/ч" -> "т/ч"
            "т/ч" -> "Гкал/ч"
            "Гкал/ч" -> "МВт"
            else -> "кг/ч"
        }
        _state.value = s.copy(
            capacityUnit = next,
            steamCapacityText = formatDisplay(kghToDisplay(s.steamCapacityKgH, next), capacityDecimals(next))
        )
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

    // ═══ Display value converters (for results section) ═══

    fun displayPressure(): String {
        val s = _state.value
        return formatDisplay(barToDisplay(s.pressureBar, s.pressureUnit), pressureDecimals(s.pressureUnit))
    }

    fun displayTemperature(): String {
        val s = _state.value
        return formatDisplay(celsiusToDisplay(s.temperatureC, s.tempUnit), 1)
    }

    fun displayCapacity(): String {
        val s = _state.value
        return formatDisplay(kghToDisplay(s.steamCapacityKgH, s.capacityUnit), capacityDecimals(s.capacityUnit))
    }

    fun displayPower(): String {
        val s = _state.value
        return when (s.powerUnit) {
            "Гкал/ч" -> Formatting.formatNumber(s.powerMW * 1.163, 4).trimEnd('0').trimEnd(',')
            "кВт" -> Formatting.formatNumber(s.powerMW * 1000.0, 1).trimEnd('0').trimEnd(',')
            else -> Formatting.formatNumber(s.powerMW, 4).trimEnd('0').trimEnd(',')
        }
    }

    // ═══ Main calculation ═══

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
