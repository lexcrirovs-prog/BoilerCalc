package ru.boilercalc.app.core.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Steam : NavRoute("steam", "Пар", Icons.Default.Whatshot)
    data object Converter : NavRoute("converter", "Конвертер", Icons.Default.SwapHoriz)
    data object HeatCalc : NavRoute("heat_calc", "Тепло", Icons.Default.Thermostat)
    data object Economics : NavRoute("economics", "Экономика", Icons.Default.TrendingUp)

    companion object {
        val all = listOf(Steam, Converter, HeatCalc, Economics)
    }
}
