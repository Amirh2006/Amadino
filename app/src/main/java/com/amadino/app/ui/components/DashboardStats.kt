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
import com.amadino.app.utils.toPersianDigits // Import the utility

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
        StatCard(
            title = "حاضرین",
            value = toPersianDigits(stats.presentCount.toString()), // Convert to Persian
            iconSymbol = "✔",
            accentColor = ElectricAqua,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "غایبین",
            value = toPersianDigits(stats.absentCount.toString()), // Convert to Persian
            iconSymbol = "✖",
            accentColor = FieryCoral,
            modifier = Modifier.weight(1f)
        )

        StatCard(
            title = "پیامک‌ها",
            value = toPersianDigits(stats.smsCount.toString()), // Convert to Persian
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