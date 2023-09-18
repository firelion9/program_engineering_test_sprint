package org.tod87et.calculator.client

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val md_theme_light_primary = Color(0xFF6750A4)
private val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFE9DDFF)
private val md_theme_light_onPrimaryContainer = Color(0xFF22005D)
private val md_theme_light_secondary = Color(0xFF883EA1)
private val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFAD7FF)
private val md_theme_light_onSecondaryContainer = Color(0xFF330045)
private val md_theme_light_tertiary = Color(0xFF7E5260)
private val md_theme_light_onTertiary = Color(0xFFFFFFFF)
private val md_theme_light_tertiaryContainer = Color(0xFFFFD9E3)
private val md_theme_light_onTertiaryContainer = Color(0xFF31101D)
private val md_theme_light_error = Color(0xFFBA1A1A)
private val md_theme_light_errorContainer = Color(0xFFFFDAD6)
private val md_theme_light_onError = Color(0xFFFFFFFF)
private val md_theme_light_onErrorContainer = Color(0xFF410002)
private val md_theme_light_background = Color(0xFFFFFBFF)
private val md_theme_light_onBackground = Color(0xFF1C1B1E)
private val md_theme_light_surface = Color(0xFFFFFBFF)
private val md_theme_light_onSurface = Color(0xFF1C1B1E)
private val md_theme_light_surfaceVariant = Color(0xFFE7E0EB)
private val md_theme_light_onSurfaceVariant = Color(0xFF49454E)
private val md_theme_light_outline = Color(0xFF7A757F)
private val md_theme_light_inverseOnSurface = Color(0xFFF4EFF4)
private val md_theme_light_inverseSurface = Color(0xFF313033)
private val md_theme_light_inversePrimary = Color(0xFFCFBCFF)
private val md_theme_light_shadow = Color(0xFF000000)
private val md_theme_light_surfaceTint = Color(0xFF6750A4)
private val md_theme_light_outlineVariant = Color(0xFFCAC4CF)
private val md_theme_light_scrim = Color(0xFF000000)

private val md_theme_dark_primary = Color(0xFFCFBCFF)
private val md_theme_dark_onPrimary = Color(0xFF381E72)
private val md_theme_dark_primaryContainer = Color(0xFF4F378A)
private val md_theme_dark_onPrimaryContainer = Color(0xFFE9DDFF)
private val md_theme_dark_secondary = Color(0xFFEFB0FF)
private val md_theme_dark_onSecondary = Color(0xFF53006E)
private val md_theme_dark_secondaryContainer = Color(0xFF6D2387)
private val md_theme_dark_onSecondaryContainer = Color(0xFFFAD7FF)
private val md_theme_dark_tertiary = Color(0xFFEFB8C8)
private val md_theme_dark_onTertiary = Color(0xFF4A2532)
private val md_theme_dark_tertiaryContainer = Color(0xFF633B48)
private val md_theme_dark_onTertiaryContainer = Color(0xFFFFD9E3)
private val md_theme_dark_error = Color(0xFFFFB4AB)
private val md_theme_dark_errorContainer = Color(0xFF93000A)
private val md_theme_dark_onError = Color(0xFF690005)
private val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
private val md_theme_dark_background = Color(0xFF1C1B1E)
private val md_theme_dark_onBackground = Color(0xFFE6E1E6)
private val md_theme_dark_surface = Color(0xFF1C1B1E)
private val md_theme_dark_onSurface = Color(0xFFE6E1E6)
private val md_theme_dark_surfaceVariant = Color(0xFF49454E)
private val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4CF)
private val md_theme_dark_outline = Color(0xFF948F99)
private val md_theme_dark_inverseOnSurface = Color(0xFF1C1B1E)
private val md_theme_dark_inverseSurface = Color(0xFFE6E1E6)
private val md_theme_dark_inversePrimary = Color(0xFF6750A4)
private val md_theme_dark_shadow = Color(0xFF000000)
private val md_theme_dark_surfaceTint = Color(0xFFCFBCFF)
private val md_theme_dark_outlineVariant = Color(0xFF49454E)
private val md_theme_dark_scrim = Color(0xFF000000)


private val seed = Color(0xFF6750A4)


private val colors = lightColors(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryVariant = md_theme_light_tertiary,
    error = md_theme_light_error,
    onError = md_theme_light_onError,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
)

@Composable
fun CalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = colors, content = content)
}