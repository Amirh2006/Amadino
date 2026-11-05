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
import com.amadino.app.ui.theme.ElectricAqua
import com.amadino.app.ui.theme.FieryCoral
import com.amadino.app.ui.theme.SoftLilac

data class DashboardStats(
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val smsCount: Int = 0
)

@Composable
fun DashboardStatsRow(stats: DashboardStats, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Present Card
        StatCard(
            title = "حاضرین",
            value = stats.presentCount.toString(),
            iconSymbol = "✔",
            accentColor = ElectricAqua,
            modifier = Modifier.weight(1f)
        )

        // Absent Card
        StatCard(
            title = "غایبین",
            value = stats.absentCount.toString(),
            iconSymbol = "✖",
            accentColor = FieryCoral,
            modifier = Modifier.weight(1f)
        )

        // SMS Card
        StatCard(
            title = "پیامک‌ها",
            value = stats.smsCount.toString(),
            iconSymbol = "✉",
            accentColor = SoftLilac,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    iconSymbol: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = accentColor.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Using Unicode symbols instead of Material Icons to avoid dependency issues
            Text(
                text = iconSymbol,
                color = accentColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = title,
                color = SoftLilac,
                fontSize = 12.sp
            )
        }
    }
}