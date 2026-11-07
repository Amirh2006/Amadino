package com.amadino.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.ui.theme.LocalAppColors // Import dynamic colors

@Composable
fun GlassButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current // Get current theme colors

    Box(
        modifier = modifier
            .background(colors.glassSurface, RoundedCornerShape(12.dp))
            .border(1.dp, colors.textPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = colors.textPrimary, fontSize = 14.sp)
    }
}