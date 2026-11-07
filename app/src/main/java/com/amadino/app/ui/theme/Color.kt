package com.amadino.app.ui.theme

import androidx.compose.ui.graphics.Color

// Data class to hold all colors for a specific theme
data class AppColors(
    val background: Color,
    val accent: Color,
    val success: Color,
    val alert: Color,
    val textPrimary: Color,
    val glassSurface: Color,
    val glassBorder: Color,
    val isLight: Boolean // Helps us adjust UI elements (like icons) based on theme
)

// 1. Cyber-Violet (Current Default Dark)
val CyberVioletColors = AppColors(
    background = Color(0xFF0A071B),
    accent = Color(0xFF7B2CBF),
    success = Color(0xFF00F5D4),
    alert = Color(0xFFFF6B35),
    textPrimary = Color(0xFFE0AAFF),
    glassSurface = Color(0x33FFFFFF),
    glassBorder = Color(0x4DE0AAFF),
    isLight = false
)

// 2. Solar Dawn (Light Theme)
val SolarDawnColors = AppColors(
    background = Color(0xFFF5F0FC),
    accent = Color(0xFF5E17EB),
    success = Color(0xFF00B395),
    alert = Color(0xFFE63946),
    textPrimary = Color(0xFF140A2E),
    glassSurface = Color(0x147B2CBF), // 8% opacity purple
    glassBorder = Color(0x335E17EB),  // Subtle purple border
    isLight = true
)

// 3. Emerald Matrix (Dark Theme)
val EmeraldMatrixColors = AppColors(
    background = Color(0xFF05140D),
    accent = Color(0xFF00E676),
    success = Color(0xFF00E5FF),
    alert = Color(0xFFFF3D00),
    textPrimary = Color(0xFFB2DFDB),
    glassSurface = Color(0x3300E676),
    glassBorder = Color(0x4D00E676),
    isLight = false
)