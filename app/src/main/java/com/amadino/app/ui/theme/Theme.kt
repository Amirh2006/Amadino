package com.amadino.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// This allows us to access our custom colors anywhere in the app
val LocalAppColors = staticCompositionLocalOf { CyberVioletColors }

@Composable
fun AmadinoTheme(
    themeName: String = "CyberViolet",
    content: @Composable () -> Unit
) {
    // Select colors based on the chosen theme
    val colors = when (themeName) {
        "SolarDawn" -> SolarDawnColors
        "EmeraldMatrix" -> EmeraldMatrixColors
        else -> CyberVioletColors
    }

    // Define Material3 color scheme dynamically
    val materialColors = if (colors.isLight) {
        lightColorScheme(
            primary = colors.accent,
            secondary = colors.success,
            tertiary = colors.alert,
            background = colors.background,
            surface = colors.background,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary
        )
    } else {
        darkColorScheme(
            primary = colors.accent,
            secondary = colors.success,
            tertiary = colors.alert,
            background = colors.background,
            surface = colors.background,
            onPrimary = Color.White,
            onSecondary = colors.background,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary
        )
    }

    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}