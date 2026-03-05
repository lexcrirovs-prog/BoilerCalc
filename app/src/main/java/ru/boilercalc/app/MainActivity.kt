package ru.boilercalc.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import ru.boilercalc.app.core.theme.BoilerCalcTheme
import ru.boilercalc.app.core.theme.ThemeMode
import ru.boilercalc.app.core.theme.ThemePreference
import ru.boilercalc.app.core.ui.navigation.AppNavHost
import ru.boilercalc.app.core.ui.navigation.BottomNavBar
import ru.boilercalc.app.core.ui.navigation.NavRoute
import ru.boilercalc.app.core.util.UtmParser

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "BoilerCalc"
        /** Stored UTM parameters from the deep link that launched the app. */
        var utmParams: Map<String, String> = emptyMap()
            private set
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Parse UTM from the launching intent
        handleDeepLink(intent)

        setContent {
            val themePreference = remember { ThemePreference(applicationContext) }
            val themeMode by themePreference.themeMode.collectAsState(initial = ThemeMode.DARK)

            BoilerCalcTheme(themeMode = themeMode) {
                val navController = rememberNavController()

                // Navigate to the target tab specified by the deep link path
                val deepLinkTab = remember { getDeepLinkTab(intent) }
                LaunchedEffect(deepLinkTab) {
                    if (deepLinkTab != null) {
                        navController.navigate(deepLinkTab) {
                            popUpTo(NavRoute.Steam.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                val scope = rememberCoroutineScope()
                var showThemeMenu by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                            Image(
                                painter = painterResource(R.drawable.logo_pg_2),
                                contentDescription = "Premium Gas Company",
                                modifier = Modifier.height(36.dp),
                                contentScale = ContentScale.Fit
                            )
                        },
                            actions = {
                                IconButton(onClick = { showThemeMenu = true }) {
                                    Icon(
                                        Icons.Default.Palette,
                                        contentDescription = "Сменить тему"
                                    )
                                }
                                DropdownMenu(
                                    expanded = showThemeMenu,
                                    onDismissRequest = { showThemeMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Тёмная") },
                                        onClick = {
                                            scope.launch { themePreference.setThemeMode(ThemeMode.DARK) }
                                            showThemeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Светлая") },
                                        onClick = {
                                            scope.launch { themePreference.setThemeMode(ThemeMode.LIGHT) }
                                            showThemeMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Латте") },
                                        onClick = {
                                            scope.launch { themePreference.setThemeMode(ThemeMode.LATTE) }
                                            showThemeMenu = false
                                        }
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = { BottomNavBar(navController) }
                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data ?: return
        utmParams = UtmParser.parse(uri)
        if (utmParams.isNotEmpty()) {
            Log.d(TAG, "UTM params: $utmParams")
        }
    }

    /**
     * Determine the target navigation tab from the deep link.
     * Supports paths like:
     *   /app/steam  → Tab 1
     *   /app/converter → Tab 2
     *   /app/heat → Tab 3
     *   /app/economics → Tab 4
     * Also works with custom scheme: boilercalc://steam
     */
    private fun getDeepLinkTab(intent: Intent?): String? {
        val uri = intent?.data ?: return null
        val path = uri.path?.trimEnd('/') ?: uri.host ?: return null
        val segment = path.substringAfterLast('/')

        return when (segment.lowercase()) {
            "steam", "пар" -> NavRoute.Steam.route
            "converter", "конвертер" -> NavRoute.Converter.route
            "heat", "heatcalc", "тепло" -> NavRoute.HeatCalc.route
            "economics", "экономика" -> NavRoute.Economics.route
            else -> null
        }
    }
}
