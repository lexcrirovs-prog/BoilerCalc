package ru.boilercalc.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline
)

private val LatteColorScheme = lightColorScheme(
    primary = LattePrimary,
    onPrimary = LatteOnPrimary,
    primaryContainer = LattePrimaryContainer,
    onPrimaryContainer = LatteOnPrimaryContainer,
    secondary = LatteSecondary,
    onSecondary = LatteOnSecondary,
    secondaryContainer = LatteSecondaryContainer,
    onSecondaryContainer = LatteOnSecondaryContainer,
    tertiary = LatteTertiary,
    onTertiary = LatteOnTertiary,
    tertiaryContainer = LatteTertiaryContainer,
    onTertiaryContainer = LatteOnTertiaryContainer,
    error = LatteError,
    onError = LatteOnError,
    background = LatteBackground,
    onBackground = LatteOnBackground,
    surface = LatteSurface,
    onSurface = LatteOnSurface,
    surfaceVariant = LatteSurfaceVariant,
    onSurfaceVariant = LatteOnSurfaceVariant,
    outline = LatteOutline
)

@Composable
fun BoilerCalcTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.LATTE -> LatteColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = themeMode != ThemeMode.DARK
            insetsController.isAppearanceLightNavigationBars = themeMode != ThemeMode.DARK
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BoilerCalcTypography,
        content = content
    )
}
