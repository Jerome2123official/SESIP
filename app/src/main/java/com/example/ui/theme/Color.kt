package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Premium Sheshan Academy Theme Colors
val NavalBlue = Color(0xFF0A192F)       // Deep Midnight Blue (Background)
val AcademyBlue = Color(0xFF172A45)     // Deep Blue Surface Card
val AccentBlue = Color(0xFF3066BE)      // Bright Blue (Interactive Buttons/Links)
val RoyalGold = Color(0xFFD4AF37)       // Luxury Metallic Gold (Primary Accents)
val VibrantGold = Color(0xFFFFD700)     // Bright Vibrant Gold
val WarmGold = Color(0xFFEAA135)        // Warm Honey Gold (Secondary Highlight)

// Geometric Balance Theme Colors
var isDarkThemeGlobal: Boolean = false

val GeoPrimary: Color
    get() = if (isDarkThemeGlobal) RoyalGold else Color(0xFF1E3A8A)

val GeoGold: Color
    get() = if (isDarkThemeGlobal) VibrantGold else Color(0xFFD4AF37)

val GeoBackground: Color
    get() = if (isDarkThemeGlobal) NavalBlue else Color(0xFFF8FAFC)

val GeoSurface: Color
    get() = if (isDarkThemeGlobal) AcademyBlue else Color(0xFFFFFFFF)

val GeoSlate: Color
    get() = if (isDarkThemeGlobal) Color(0xFF112240) else Color(0xFFF1F5F9)

val GeoBorder: Color
    get() = if (isDarkThemeGlobal) Color(0xFF233554) else Color(0xFFE2E8F0)

// Light Theme Equivalents
val LightBackground = Color(0xFFF8FAFC)  // Clean Cool Slate-White
val LightSurface = Color(0xFFFFFFFF)     // Clean White Card
val LightSecondary = Color(0xFFE1EBF5)   // Light Blue-Grey Fill

// Neutral text & UI accents
val GoldAccentLight = Color(0xFFFFF9E6)  // Light Warm Gold Tint
val DarkCardColor = Color(0xFF112240)    // High contrast dark background component
val TextOnDark = Color(0xFFF8F9FA)      // Almost Pure White
val TextSecondaryOnDark = Color(0xFF8892B0) // Cool muted grey-blue for subtitles
val DarkBorder = Color(0xFF233554)       // Clean dark border
val SuccessGreen = Color(0xFF2EC4B6)     // Vibrant Teal/Green
val AlertRed = Color(0xFFE71D36)         // Alert Red

val GeoText: Color
    get() = if (isDarkThemeGlobal) Color.White else Color.Black

