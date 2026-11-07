package com.amadino.app.ui.attendance // Replace with your actual package name

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.domain.model.AttendanceStatus
import com.amadino.app.domain.model.SlotAttendance
import com.amadino.app.domain.model.StudentAttendanceDetail
import com.amadino.app.ui.components.GlassCard
import com.amadino.app.ui.theme.LocalAppColors // Import dynamic colors

@Composable
fun AttendanceDetailScreen(
    studentDetail: StudentAttendanceDetail,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current // Get current theme colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // Header: Student Info & Date
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = studentDetail.studentName,
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "تاریخ: ${studentDetail.date}",
                    color = colors.textPrimary.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("زنگ کلاسی", color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 12.sp)
            Text("وضعیت", color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 12.sp)
            Text("ساعت ورود/خروج", color = colors.textPrimary.copy(alpha = 0.7f), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of Slots
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(studentDetail.slots) { slot ->
                SlotDetailRow(slot)
            }
        }
    }
}

@Composable
fun SlotDetailRow(slot: SlotAttendance) {
    val colors = LocalAppColors.current

    // Determine color based on status
    val statusColor = when (slot.status) {
        AttendanceStatus.PRESENT -> colors.success
        AttendanceStatus.LATE -> colors.textPrimary
        AttendanceStatus.ABSENT -> colors.alert
        AttendanceStatus.NOT_RECORDED -> colors.textPrimary.copy(alpha = 0.5f)
    }

    val statusText = when (slot.status) {
        AttendanceStatus.PRESENT -> "حاضر"
        AttendanceStatus.LATE -> "تاخیر"
        AttendanceStatus.ABSENT -> "غایب"
        AttendanceStatus.NOT_RECORDED -> "ثبت‌نشده"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = statusColor.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Slot Time
            Text(
                text = slot.slotTime,
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Status Badge
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Check-in / Check-out Times
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "ورود: ${slot.checkInTime ?: "--"}",
                    color = colors.textPrimary.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
                Text(
                    text = "خروج: ${slot.checkOutTime ?: "--"}",
                    color = colors.textPrimary.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}