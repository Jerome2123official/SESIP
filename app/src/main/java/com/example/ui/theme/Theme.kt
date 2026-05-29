package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RoyalGold,
    secondary = VibrantGold,
    tertiary = AccentBlue,
    background = NavalBlue,
    surface = AcademyBlue,
    onPrimary = NavalBlue,
    onSecondary = NavalBlue,
    onBackground = TextOnDark,
    onSurface = TextOnDark,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = GeoPrimary,
    secondary = GeoGold,
    tertiary = AccentBlue,
    background = GeoBackground,
    surface = GeoSurface,
    onPrimary = Color.White,
    onSecondary = GeoPrimary,
    onBackground = GeoPrimary,
    onSurface = GeoPrimary,
    outline = GeoBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic coloring since we have a strict brand guideline (Dark Blue + Gold)
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    isDarkThemeGlobal = darkTheme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
