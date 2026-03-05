package ru.boilercalc.app.feature.economics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.boilercalc.app.core.data.EconBoilerDatabase
import ru.boilercalc.app.core.domain.EconomicsCalculationEngine
import ru.boilercalc.app.core.model.EconBoilerModel
import ru.boilercalc.app.core.util.Formatting

class EconomicsViewModel : ViewModel() {

    private val _state = MutableStateFlow(EconomicsState())
    val state: StateFlow<EconomicsState> = _state.asStateFlow()

    fun getModelsForCategory(category: String): List<EconBoilerModel> {
        return when (category) {
            "steam" -> EconBoilerDatabase.steam
            "water" -> EconBoilerDatabase.water
            "waterE" -> EconBoilerDatabase.waterE
            else -> emptyList()
        }
    }

    fun selectCategory(category: String) {
        _state.update {
            it.copy(
                boilerCategory = category,
                selectedModel = null,
                useEconomizer = false,
                capex = 0.0,
                isCalculated = false
            )
        }
    }

    fun selectModel(model: EconBoilerModel) {
        _state.update { current ->
            val capex = calculateCapex(model, current.boilerCount, current.useEconomizer)
            current.copy(
                selectedModel = model,
                capex = capex,
                isCalculated = false
            )
        }
        recalculateOPEX()
    }

    fun onBoilerCountChange(count: Int) {
        val validCount = count.coerceIn(1, 20)
        _state.update { current ->
            val capex = current.selectedModel?.let {
                calculateCapex(it, validCount, current.useEconomizer)
            } ?: 0.0
            current.copy(
                boilerCount = validCount,
                capex = capex,
                isCalculated = false
            )
        }
        recalculateOPEX()
    }

    fun onUseEconomizerChange(enabled: Boolean) {
        _state.update { current ->
            val capex = current.selectedModel?.let {
                calculateCapex(it, current.boilerCount, enabled)
            } ?: 0.0
            current.copy(
                useEconomizer = enabled,
                capex = capex,
                isCalculated = false
            )
        }
    }

    fun onLoadChange(text: String) {
        val value = text.replace(",", ".").toDoubleOrNull()
        _state.update {
            it.copy(
                loadText = text,
                loadPercent = (value ?: it.loadPercent * 100.0).let { v -> v / 100.0 },
                isCalculated = false
            )
        }
        if (value != null) recalculateOPEX()
    }

    fun onDailyHoursChange(text: String) {
        val value = text.replace(",", ".").toDoubleOrNull()
        _state.update {
            it.copy(
                dailyHoursText = text,
                dailyHours = value?.coerceIn(0.0, 24.0) ?: it.dailyHours,
                isCalculated = false
            )
        }
        if (value != null) recalculateOPEX()
    }

    fun onWorkDaysChange(text: String) {
        val value = text.toIntOrNull()
        _state.update {
            it.copy(
                workDaysText = text,
                workDays = value?.coerceIn(0, 366) ?: it.workDays,
                isCalculated = false
            )
        }
        if (value != null) recalculateOPEX()
    }

    fun onGasPriceChange(text: String) {
        val value = text.replace(",", ".").toDoubleOrNull()
        _state.update {
            it.copy(
                gasPriceText = text,
                gasPrice = value ?: it.gasPrice,
                isCalculated = false
            )
        }
        if (value != null) recalculateOPEX()
    }

    fun onMaintenanceChange(text: String) {
        val value = Formatting.parseMoney(text)
        _state.update {
            it.copy(
                maintenanceCostText = text,
                maintenanceCost = value,
                isCalculated = false
            )
        }
        recalculateOPEX()
    }

    fun onRevenueModeChange(mode: String) {
        _state.update { it.copy(revenueMode = mode, isCalculated = false) }
    }

    fun onUniformRevenueChange(text: String) {
        val value = Formatting.parseMoney(text)
        _state.update {
            it.copy(
                uniformRevenueText = text,
                uniformRevenue = value,
                isCalculated = false
            )
        }
    }

    fun onVariableRevenueChange(year: Int, text: String) {
        val value = Formatting.parseMoney(text)
        _state.update { current ->
            val updated = current.variableRevenues.toMutableList()
            if (year in updated.indices) {
                updated[year] = value
            }
            current.copy(variableRevenues = updated, isCalculated = false)
        }
    }

    fun onYearsCountChange(years: Int) {
        val valid = years.coerceIn(1, 30)
        _state.update { current ->
            val revenues = if (valid > current.variableRevenues.size) {
                current.variableRevenues + List(valid - current.variableRevenues.size) { 0.0 }
            } else {
                current.variableRevenues.take(valid)
            }
            current.copy(
                yearsCount = valid,
                variableRevenues = revenues,
                isCalculated = false
            )
        }
    }

    fun onDiscountRateChange(text: String) {
        val value = text.replace(",", ".").toDoubleOrNull()
        _state.update {
            it.copy(
                discountRateText = text,
                discountRate = (value ?: it.discountRate * 100.0).let { v -> v / 100.0 },
                isCalculated = false
            )
        }
    }

    fun onTarifGcalChange(text: String) {
        val value = text.replace(",", ".").toDoubleOrNull() ?: 0.0
        _state.update { current ->
            val grossRevenue = if (value > 0 && current.annualHeatGcal > 0) {
                value * current.annualHeatGcal
            } else {
                current.uniformRevenue
            }
            val revenueText = if (value > 0 && current.annualHeatGcal > 0) {
                Formatting.formatNumber(grossRevenue, 0)
            } else {
                current.uniformRevenueText
            }
            current.copy(
                tarifGcalText = text,
                tarifGcal = value,
                uniformRevenue = grossRevenue,
                uniformRevenueText = revenueText,
                isCalculated = false
            )
        }
    }

    private fun recalculateOPEX() {
        val current = _state.value
        val model = current.selectedModel ?: return

        val annualGas = EconomicsCalculationEngine.calcAnnualGas(
            maxGas = model.maxGas,
            loadPercent = current.loadPercent,
            dailyHours = current.dailyHours,
            workDays = current.workDays,
            boilerCount = current.boilerCount
        )

        val annualOPEX = EconomicsCalculationEngine.calcOPEX(
            maxGas = model.maxGas,
            loadPercent = current.loadPercent,
            dailyHours = current.dailyHours,
            workDays = current.workDays,
            gasPrice = current.gasPrice,
            maintenanceCost = current.maintenanceCost,
            boilerCount = current.boilerCount,
            efficiency = model.efficiency
        )

        val annualHeatGcal = EconomicsCalculationEngine.calcAnnualHeatGcal(
            annualGas = annualGas,
            efficiency = model.efficiency
        )

        _state.update { current2 ->
            val grossRevenue = if (current2.tarifGcal > 0 && annualHeatGcal > 0) {
                current2.tarifGcal * annualHeatGcal
            } else {
                current2.uniformRevenue
            }
            val revenueText = if (current2.tarifGcal > 0 && annualHeatGcal > 0) {
                Formatting.formatNumber(grossRevenue, 0)
            } else {
                current2.uniformRevenueText
            }
            current2.copy(
                annualGas = annualGas,
                annualOPEX = annualOPEX,
                annualHeatGcal = annualHeatGcal,
                uniformRevenue = grossRevenue,
                uniformRevenueText = revenueText
            )
        }
    }

    private fun calculateCapex(
        model: EconBoilerModel,
        count: Int,
        useEconomizer: Boolean
    ): Double {
        val boilerCost = model.price.toDouble() * count
        val economizerCost = if (useEconomizer && model.economizerPrice != null) {
            model.economizerPrice.toDouble() * count
        } else {
            0.0
        }
        return boilerCost + economizerCost
    }

    fun calculate() {
        val current = _state.value
        val model = current.selectedModel ?: return

        // Ensure OPEX is up to date
        recalculateOPEX()
        val updatedState = _state.value

        // Build revenues list
        val revenues = if (current.revenueMode == "uniform") {
            List(current.yearsCount) { current.uniformRevenue }
        } else {
            current.variableRevenues.take(current.yearsCount)
        }

        val capex = calculateCapex(model, current.boilerCount, current.useEconomizer)

        val paybackResult = EconomicsCalculationEngine.calcPayback(
            capex = capex,
            revenues = revenues,
            opex = updatedState.annualOPEX,
            discountRate = current.discountRate
        )

        _state.update {
            it.copy(
                capex = capex,
                paybackResult = paybackResult,
                isCalculated = true
            )
        }
    }
}
