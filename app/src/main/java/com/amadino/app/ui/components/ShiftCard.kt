package com.amadino.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amadino.app.data.local.entity.ShiftEntity

@Composable
fun ShiftCard(
    shift: ShiftEntity,
    onTakeAttendance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = shift.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "ساعت ${formatTimeRange(shift.startTime, shift.endTime)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "مسئول: ${shift.monitorName.ifBlank { "ثبت نشده" }}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.size(12.dp))

            OutlinedButton(onClick = onTakeAttendance) {
                Text(text = "ثبت حضور")
            }
        }
    }
}
