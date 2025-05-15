package com.example.juakaliconnect.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A237E), // Deep indigo
    onPrimary = Color.White,
    secondary = Color(0xFF3949AB), // Slightly lighter indigo
    onSecondary = Color.White,
    background = Color(0xFFF5F5F5), // Elegant light gray
    onBackground = Color(0xFF1A1A1A), // Rich dark text
    surface = Color.White,
    onSurface = Color(0xFF1A1A1A)
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8C9EFF), // Light elegant indigo
    onPrimary = Color.Black,
    secondary = Color(0xFF536DFE), // Soft blue indigo
    onSecondary = Color.Black,
    background = Color(0xFF121212), // Deep black-gray
    onBackground = Color(0xFFE0E0E0), // Muted white text
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)
