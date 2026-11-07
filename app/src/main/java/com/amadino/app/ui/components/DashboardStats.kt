package com.amadino.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.ui.theme.LocalAppColors
import com.amadino.app.utils.toPersianDigits

data class DashboardStats(
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val smsCount: Int = 0
)

@Composable
fun DashboardStatsRow(stats: DashboardStats, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard("حاضرین", toPersianDigits(stats.presentCount.toString()), "✔", colors.success, Modifier.weight(1f))
        StatCard("غایبین", toPersianDigits(stats.absentCount.toString()), "✖", colors.alert, Modifier.weight(1f))
        StatCard("پیامک‌ها", toPersianDigits(stats.smsCount.toString()), "✉", colors.accent, Modifier.weight(1f))
    }
}

@Composable
fun StatCard(title: String, value: String, iconSymbol: String, accentColor: Color, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    GlassCard(modifier = modifier, borderColor = accentColor.copy(alpha = 0.4f)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = iconSymbol, color = accentColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = colors.textPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = title, color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 12.sp)
        }
    }
}