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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.ui.theme.LocalAppColors

@Composable
fun GlassButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Box(
        modifier = modifier
            .background(colors.glassSurface, RoundedCornerShape(12.dp))
            .border(1.dp, colors.textPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 10.dp), // کاهش پدینگ افقی
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colors.textPrimary,
            fontSize = 12.sp, // فونت کمی کوچک‌تر
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1 // جلوگیری از شکستن متن به خط بعد
        )
    }
}