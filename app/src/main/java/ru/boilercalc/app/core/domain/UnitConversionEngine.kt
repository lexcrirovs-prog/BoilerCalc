package ru.boilercalc.app.core.domain

import ru.boilercalc.app.core.data.UnitDefinitions

object UnitConversionEngine {

    /**
     * Convert value between units within the same group.
     *
     * @param groupIndex Index of the unit group (0..12)
     * @param fromUnitIndex Index of the source unit within the group
     * @param toUnitIndex Index of the target unit within the group
     * @param value Value to convert
     * @return Converted value
     */
    fun convert(
        groupIndex: Int,
        fromUnitIndex: Int,
        toUnitIndex: Int,
        value: Double
    ): Double {
        val groups = UnitDefinitions.groups
        if (groupIndex !in groups.indices) return value

        val group = groups[groupIndex]
        if (fromUnitIndex !in group.units.indices) return value
        if (toUnitIndex !in group.units.indices) return value

        if (fromUnitIndex == toUnitIndex) return value

        val baseValue = group.units[fromUnitIndex].toBase(value)
        return group.units[toUnitIndex].fromBase(baseValue)
    }

    /**
     * Convert by unit names within a group.
     */
    fun convert(
        groupIndex: Int,
        fromUnitName: String,
        toUnitName: String,
        value: Double
    ): Double {
        val groups = UnitDefinitions.groups
        if (groupIndex !in groups.indices) return value

        val group = groups[groupIndex]
        val fromIdx = group.units.indexOfFirst { it.name == fromUnitName }
        val toIdx = group.units.indexOfFirst { it.name == toUnitName }

        if (fromIdx < 0 || toIdx < 0) return value
        return convert(groupIndex, fromIdx, toIdx, value)
    }
}
