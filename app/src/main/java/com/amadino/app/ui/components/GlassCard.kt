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
import com.amadino.app.ui.theme.LocalAppColors

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Int = 24,
    borderColor: Color = LocalAppColors.current.glassBorder,
    content: @Composable () -> Unit
) {
    val colors = LocalAppColors.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.glassSurface,
                        colors.glassSurface.copy(alpha = colors.glassSurface.alpha * 0.5f)
                    )
                )
            )
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(cornerRadius.dp)
            )
            .padding(16.dp)
    ) {
        content()
    }
}