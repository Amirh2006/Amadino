package com.amadino.app.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val schoolDays by viewModel.schoolDays.collectAsState()
    val shifts by viewModel.shiftsByDay.collectAsState()
    val smsCounter by viewModel.smsCounter.collectAsState()
    val selectedDayId by viewModel.selectedDayId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Amadino") },
                colors = TopAppBarDefaults.topAppBarColors()
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SMS: ${smsCounter?.totalSent ?: 0}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${smsCounter?.totalFailed ?: 0} failed · ${smsCounter?.totalSkipped ?: 0} skipped",
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
