package ru.boilercalc.app.core.domain

import ru.boilercalc.app.core.data.BoilerCatalogue
import ru.boilercalc.app.core.model.BoilerModel
import ru.boilercalc.app.core.model.WaterBoilerModel

data class SelectedSteamBoiler(
    val model: BoilerModel,
    val count: Int,
    val loadPercent: Double
)

data class SelectedWaterBoiler(
    val model: WaterBoilerModel,
    val count: Int
)

object BoilerSelectionEngine {

    /**
     * Cascade selection of steam boilers.
     * Selects combination of boilers to cover required capacity.
     * Excludes the S-15000 model from automatic selection (it's for manual quotes only).
     *
     * @param requiredKgH Required steam capacity, kg/h
     * @return List of selected boilers with counts and load percentages
     */
    fun selectSteamBoilers(requiredKgH: Double): List<SelectedSteamBoiler> {
        if (requiredKgH <= 0) return emptyList()

        // Exclude S-15000 from automatic selection
        val catalogue = BoilerCatalogue.steamBoilers.filter { it.id != "s15000" }
        if (catalogue.isEmpty()) return emptyList()

        val result = mutableListOf<SelectedSteamBoiler>()
        var remaining = requiredKgH

        while (remaining > 0) {
            // Find the smallest boiler that can cover the remaining capacity
            val suitable = catalogue.firstOrNull { it.maxSteam >= remaining }

            if (suitable != null) {
                val load = remaining / suitable.maxSteam
                result.add(SelectedSteamBoiler(suitable, 1, load * 100.0))
                remaining = 0.0
            } else {
                // Use the largest available boiler
                val largest = catalogue.last()
                val count = (remaining / largest.maxSteam).toInt()
                if (count > 0) {
                    result.add(SelectedSteamBoiler(largest, count, 100.0))
                    remaining -= count * largest.maxSteam
                } else {
                    // Remaining is less than largest but no exact match — use largest
                    result.add(SelectedSteamBoiler(largest, 1, (remaining / largest.maxSteam) * 100.0))
                    remaining = 0.0
                }
            }
        }

        return result
    }

    /**
     * Smart selection of water boilers with N+1 redundancy.
     *
     * Case 1: Q < 250 kW → 1× Premium C-250
     * Case 2: 250 ≤ Q ≤ 20000 kW → 2 boilers with ~50% redundancy
     * Case 3: Q > 20000 kW → ceil(Q/10000) × Premium E-10000
     *
     * @param powerKW Required power, kW
     * @return List of selected water boilers with counts
     */
    fun selectWaterBoilers(powerKW: Double): List<SelectedWaterBoiler> {
        if (powerKW <= 0) return emptyList()
        val catalogue = BoilerCatalogue.waterBoilers

        // Case 1: Small load
        if (powerKW < 250) {
            val smallest = catalogue.first()
            return listOf(SelectedWaterBoiler(smallest, 1))
        }

        // Case 3: Very large load
        if (powerKW > 20000) {
            val largest = catalogue.last()
            val count = kotlin.math.ceil(powerKW / largest.power.toDouble()).toInt()
            return listOf(SelectedWaterBoiler(largest, count))
        }

        // Case 2: Standard selection with 50% redundancy
        val half = powerKW / 2.0
        val lowerIdx = catalogue.indexOfLast { it.power <= half }
        val upperIdx = catalogue.indexOfFirst { it.power >= half }

        if (lowerIdx < 0 || upperIdx < 0) {
            // Fallback: use the smallest suitable
            val suitable = catalogue.firstOrNull { it.power >= powerKW }
                ?: catalogue.last()
            return listOf(SelectedWaterBoiler(suitable, 2))
        }

        val lower = catalogue[lowerIdx]
        val upper = catalogue[upperIdx]

        return if (lower.power + upper.power >= powerKW) {
            listOf(
                SelectedWaterBoiler(lower, 1),
                SelectedWaterBoiler(upper, 1)
            )
        } else {
            listOf(SelectedWaterBoiler(upper, 2))
        }
    }
}
