package com.amadino.app.ui.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.ui.theme.ElectricAqua
import com.amadino.app.ui.theme.FieryCoral
import com.amadino.app.ui.theme.SoftLilac

/**
 * Data class to hold the dashboard statistics.
 * In the next phases, this will be populated from the users.json / Database.
 */
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
            icon = androidx.compose.material.icons.Icons.Default.CheckCircle,
            accentColor = ElectricAqua,
            modifier = Modifier.weight(1f)
        )

        // Absent Card
        StatCard(
            title = "غایبین",
            value = stats.absentCount.toString(),
            icon = androidx.compose.material.icons.Icons.Default.Cancel,
            accentColor = FieryCoral,
            modifier = Modifier.weight(1f)
        )

        // SMS Card
        StatCard(
            title = "پیامک‌ها",
            value = stats.smsCount.toString(),
            icon = androidx.compose.material.icons.Icons.Default.Email,
            accentColor = SoftLilac,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    // Using the GlassCard component created in the previous phase
    GlassCard(
        modifier = modifier,
        borderColor = accentColor.copy(alpha = 0.4f) // Subtle glowing border matching the accent
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(28.dp)
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