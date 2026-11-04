package com.amadino.app.ui.theme 
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Enforce dark theme with the Cyber-Violet palette
private val CyberVioletColorScheme = darkColorScheme(
    primary = CyberPurple,
    secondary = ElectricAqua,
    tertiary = FieryCoral,
    background = DeepVelvetDark,
    surface = DeepVelvetDark,
    onPrimary = Color.White,
    onSecondary = DeepVelvetDark,
    onBackground = SoftLilac,
    onSurface = SoftLilac
)

@Composable
fun AmadinoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CyberVioletColorScheme,
        content = content
    )
}