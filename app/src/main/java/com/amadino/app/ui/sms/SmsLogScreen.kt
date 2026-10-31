package com.amadino.app.ui.sms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.SmsCounterChip
import com.amadino.app.ui.components.SmsStatusChip
import com.amadino.app.ui.components.formatDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsLogScreen(
    viewModel: SmsLogViewModel,
    onBack: () -> Unit
) {
    val rows by viewModel.rows.collectAsState()
    val counter by viewModel.counter.collectAsState()
    val filter by viewModel.filter.collectAsState()
    var selectedLog by remember { mutableStateOf<SmsLogEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "گزارش پیامک‌ها") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "بازگشت") }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterOption(
                    label = "همه",
                    selected = filter == SmsLogViewModel.EventFilter.ALL,
                    onClick = { viewModel.setFilter(SmsLogViewModel.EventFilter.ALL) }
                )
                FilterOption(
                    label = "ورود",
                    selected = filter == SmsLogViewModel.EventFilter.ENTRY,
                    onClick = { viewModel.setFilter(SmsLogViewModel.EventFilter.ENTRY) }
                )
                FilterOption(
                    label = "خروج",
                    selected = filter == SmsLogViewModel.EventFilter.EXIT,
                    onClick = { viewModel.setFilter(SmsLogViewModel.EventFilter.EXIT) }
                )
            }

            SmsCounterChip(
                counter = counter,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (rows.isEmpty()) {
                EmptyState(
                    title = "پیامکی ثبت نشده است",
                    message = "پس از ارسال پیامک، سابقه آن در این بخش نمایش داده می‌شود."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = rows, key = { it.log.id }) { row ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLog = row.log }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = row.studentName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    SmsStatusChip(status = row.log.status)
                                }
                                Text(
                                    text = "${row.log.eventType} · ${row.log.destination}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = formatDateTime(row.log.sentAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    selectedLog?.let { log ->
        AlertDialog(
            onDismissRequest = { selectedLog = null },
            title = { Text(text = "متن پیامک") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = log.messageBody)
                    Text(
                        text = "گیرنده: ${log.destination}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    log.errorReason?.let { reason ->
                        Text(
                            text = "خطا: $reason",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (log.fallbackUsed) {
                        Text(
                            text = "از شماره دوم استفاده شده است.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedLog = null }) { Text(text = "بستن") }
            }
        )
    }
}

@Composable
private fun FilterOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(50),
        color = if (selected) colors.primary else colors.surfaceVariant,
        contentColor = if (selected) colors.onPrimary else colors.onSurfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
