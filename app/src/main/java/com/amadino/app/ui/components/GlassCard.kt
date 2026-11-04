package com.amadino.app.ui.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.theme.CyberPurple
import com.amadino.app.ui.theme.GlassBorder
import com.amadino.app.ui.theme.GlassSurface

/**
 * A reusable Glassmorphic Card component.
 * Provides a semi-transparent background, a glowing neon border, and rounded corners.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 24,
    borderColor: Color = GlassBorder,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            // Semi-transparent background for the glass effect
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassSurface.copy(alpha = 0.15f),
                        GlassSurface.copy(alpha = 0.05f)
                    )
                )
            )
            // Glowing neon border
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}

