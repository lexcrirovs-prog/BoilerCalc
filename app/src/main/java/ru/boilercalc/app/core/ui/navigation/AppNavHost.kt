package ru.boilercalc.app.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.boilercalc.app.feature.converter.UnitConverterScreen
import ru.boilercalc.app.feature.economics.EconomicsScreen
import ru.boilercalc.app.feature.heatcalc.HeatCalculationScreen
import ru.boilercalc.app.feature.steam.SteamPropertiesScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Steam.route,
        modifier = modifier
    ) {
        composable(NavRoute.Steam.route) {
            SteamPropertiesScreen()
        }
        composable(NavRoute.Converter.route) {
            UnitConverterScreen()
        }
        composable(NavRoute.HeatCalc.route) {
            HeatCalculationScreen()
        }
        composable(NavRoute.Economics.route) {
            EconomicsScreen()
        }
    }
}
