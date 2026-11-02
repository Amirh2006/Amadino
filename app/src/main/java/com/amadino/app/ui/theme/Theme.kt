package com.amadino.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryCyan,
    onPrimary = PrimaryDark,
    primaryContainer = CardBg,
    onPrimaryContainer = SurfaceWhite,
    secondary = TertiaryGreen,
    onSecondary = PrimaryDark,
    secondaryContainer = CardBg,
    onSecondaryContainer = SurfaceWhite,
    tertiary = TypographyGray,
    onTertiary = PrimaryDark,
    background = DarkBg,
    onBackground = SurfaceWhite,
    surface = DarkBg,
    onSurface = SurfaceWhite,
    surfaceVariant = CardBg,
    onSurfaceVariant = TypographyGray,
)

private val LightColorScheme = lightColorScheme(
    primary = SecondaryCyan,
    onPrimary = PrimaryDark,
    primaryContainer = LightBg,
    onPrimaryContainer = PrimaryDark,
    secondary = TertiaryGreen,
    onSecondary = PrimaryDark,
    secondaryContainer = LightBg,
    onSecondaryContainer = PrimaryDark,
    tertiary = TypographyGray,
    onTertiary = PrimaryDark,
    background = LightBg,
    onBackground = PrimaryDark,
    surface = SurfaceWhite,
    onSurface = PrimaryDark,
    surfaceVariant = CardBg,
    onSurfaceVariant = TypographyGray,
)

@Composable
fun AmadinoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
