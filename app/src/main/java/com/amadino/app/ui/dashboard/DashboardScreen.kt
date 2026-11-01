package com.amadino.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.components.DaySelector
import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.QuickActionBar
import com.amadino.app.ui.components.ShiftCard
import com.amadino.app.ui.components.SmsCounterChip
import com.amadino.app.ui.components.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onOpenStudents: () -> Unit,
    onOpenFinance: () -> Unit,
    onOpenReports: () -> Unit,
    onOpenSmsLog: () -> Unit,
    onOpenSettings: () -> Unit,
    onTakeAttendance: (shiftId: Int) -> Unit
) {
    val schoolDays by viewModel.schoolDays.collectAsState()
    val shifts by viewModel.shiftsByDay.collectAsState()
    val smsCounter by viewModel.smsCounter.collectAsState()
    val selectedDayId by viewModel.selectedDayId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "آمادینو", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = formatDate(System.currentTimeMillis()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onOpenSettings) {
                        Text(text = "مدیریت")
                    }
                    SmsCounterChip(
                        counter = smsCounter,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        bottomBar = {
            QuickActionBar(
                onStudents = onOpenStudents,
                onFinance = onOpenFinance,
                onReports = onOpenReports,
                onSmsLog = onOpenSmsLog,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            DaySelector(
                days = schoolDays,
                selectedDayId = selectedDayId,
                onDaySelected = viewModel::selectDay
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (shifts.isEmpty()) {
                EmptyState(
                    title = "شیفتی برای این روز نیست",
                    message = "برای این روز شیفتی ثبت نشده است. از بخش مدیریت، شیفت اضافه کنید."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = shifts, key = { it.id }) { shift ->
                        ShiftCard(
                            shift = shift,
                            onTakeAttendance = { onTakeAttendance(shift.id) }
                        )
                    }
                }
            }
        }
    }
}
