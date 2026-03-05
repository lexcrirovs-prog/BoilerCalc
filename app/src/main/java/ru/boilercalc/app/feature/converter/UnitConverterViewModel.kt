package ru.boilercalc.app.feature.converter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.boilercalc.app.core.data.UnitDefinitions
import ru.boilercalc.app.core.domain.UnitConversionEngine
import ru.boilercalc.app.core.util.Formatting

class UnitConverterViewModel : ViewModel() {
    private val _state = MutableStateFlow(UnitConverterState())
    val state: StateFlow<UnitConverterState> = _state.asStateFlow()

    init {
        recalculate()
    }

    fun selectGroup(index: Int) {
        _state.value = _state.value.copy(
            selectedGroupIndex = index,
            fromUnitIndex = 0,
            toUnitIndex = if (UnitDefinitions.groups[index].units.size > 1) 1 else 0
        )
        recalculate()
    }

    fun selectFromUnit(index: Int) {
        _state.value = _state.value.copy(fromUnitIndex = index)
        recalculate()
    }

    fun selectToUnit(index: Int) {
        _state.value = _state.value.copy(toUnitIndex = index)
        recalculate()
    }

    fun onInputChange(text: String) {
        _state.value = _state.value.copy(inputText = text)
        recalculate()
    }

    fun swapUnits() {
        val s = _state.value
        _state.value = s.copy(
            fromUnitIndex = s.toUnitIndex,
            toUnitIndex = s.fromUnitIndex
        )
        recalculate()
    }

    fun applyPreset(fromName: String, toName: String) {
        val group = UnitDefinitions.groups[_state.value.selectedGroupIndex]
        val fromIdx = group.units.indexOfFirst { it.name == fromName }
        val toIdx = group.units.indexOfFirst { it.name == toName }
        if (fromIdx >= 0 && toIdx >= 0) {
            _state.value = _state.value.copy(fromUnitIndex = fromIdx, toUnitIndex = toIdx)
            recalculate()
        }
    }

    private fun recalculate() {
        val s = _state.value
        val input = s.inputText.replace(',', '.').toDoubleOrNull() ?: 0.0
        val result = UnitConversionEngine.convert(
            s.selectedGroupIndex,
            s.fromUnitIndex,
            s.toUnitIndex,
            input
        )
        val formatted = Formatting.formatNumber(result, 6)
            .trimEnd('0')
            .trimEnd(',')
        _state.value = s.copy(
            result = result,
            resultText = formatted
        )
    }
}
