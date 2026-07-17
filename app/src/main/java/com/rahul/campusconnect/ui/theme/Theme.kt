package com.rahul.campusconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(

    // Brand
    primary = PrimaryBlue,
    onPrimary = Color.White,

    secondary = SecondaryBlue,
    onSecondary = Color.White,

    // App Background
    background = AppBackground,
    onBackground = TextPrimary,

    // Cards / Sheets
    surface = AppSurface,
    onSurface = TextPrimary,

    // Secondary surfaces
    surfaceVariant = SurfaceBlue,
    onSurfaceVariant = TextSecondary,

    // Borders
    outline = BorderColor,

    // Errors
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(

    primary = SecondaryBlue,

    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),

    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),

    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),

    outline = Color(0xFF475569),

    error = Color(0xFFF87171)
)

@Composable
fun CampusconnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}