package com.amadino.app.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.theme.AmadinoTheme

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val schoolDays by viewModel.schoolDays.collectAsState(initial = emptyList())
    val shifts by viewModel.shiftsByDay.collectAsState(initial = emptyList())
    val smsCounter by viewModel.smsCounter.collectAsState(initial = null)
    val selectedDayId by viewModel.selectedDayId.collectAsState(initial = 1)

    AmadinoTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Amadino") },
                    colors = MaterialTheme.colorScheme.topAppBarColors()
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // SMS counter chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.foundation.layout.alignment.CenterVertically
                ) {
                    Text(
                        text = "SMS: ${smsCounter?.totalSent ?: 0}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sent ${smsCounter?.totalFailed ?: 0} failed · ${smsCounter?.totalSkipped ?: 0} skipped",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Week strip placeholder
                Text(
                    text = "Week strip (school days: ${schoolDays.size})",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Shifts for selected day
                Text(
                    text = "Shifts for day $selectedDayId",
                    style = MaterialTheme.typography.titleMedium
                )

                if (shifts.isEmpty()) {
                    Text(
                        text = "No active shifts for this day.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Column {
                        shifts.forEach { shift ->
                            Text(
                                text = "${shift.name} (${shift.monitorName})",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick actions placeholder
                Text(
                    text = "Quick actions: Students · Finance · Reports · SMS Log",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
