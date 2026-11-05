package com.amadino.app.ui.sms

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.sms.SmsDeliveryStatus

import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.SmsCounterChip
import com.amadino.app.ui.components.SmsStatusChip
import com.amadino.app.ui.components.formatDateTime
import com.amadino.app.utils.hasSmsPermission
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsLogScreen(
    viewModel: SmsLogViewModel,
    onBack: () -> Unit,
    onOpenTemplates: () -> Unit
) {
    val rows by viewModel.rows.collectAsState()
    val counter by viewModel.counter.collectAsState()
    val filter by viewModel.filter.collectAsState()
    val retryingIds by viewModel.retryingIds.collectAsState()
    val message by viewModel.message.collectAsState()
    var selectedLog by remember { mutableStateOf<SmsLogEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var pendingRetry by remember { mutableStateOf<SmsLogEntity?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val log = pendingRetry
        pendingRetry = null
        if (granted && log != null) {
            viewModel.retry(log)
        } else if (!granted) {
            viewModel.reportMessage("برای ارسال پیامک، اجازه ارسال پیامک لازم است.")
        }
    }

    val requestRetry: (SmsLogEntity) -> Unit = { log ->
        if (context.hasSmsPermission()) {
            viewModel.retry(log)
        } else {
            pendingRetry = log
            permissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "گزارش پیامک‌ها") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "بازگشت") }
                },
                actions = {
                    TextButton(onClick = onOpenTemplates) { Text(text = "قالب‌ها") }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                                row.log.deliveryStatus?.let { delivery ->
                                    Text(
                                        text = deliveryLabel(delivery),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when (delivery) {
                                            SmsDeliveryStatus.FAILED -> MaterialTheme.colorScheme.error
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                                if (row.log.status != "SUCCESS") {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = { requestRetry(row.log) },
                                            enabled = row.log.id !in retryingIds
                                        ) {
                                            Text(
                                                text = if (row.log.id in retryingIds) {
                                                    "در حال ارسال…"
                                                } else {
                                                    "ارسال مجدد"
                                                }
                                            )
                                        }
                                    }
                                }
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
                    log.deliveryStatus?.let { delivery ->
                        Text(
                            text = "وضعیت تحویل: ${deliveryLabel(delivery)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (log.status != "SUCCESS") {
                        TextButton(
                            onClick = {
                                requestRetry(log)
                                selectedLog = null
                            }
                        ) { Text(text = "ارسال مجدد") }
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

private fun deliveryLabel(status: String): String = when (status) {
    SmsDeliveryStatus.DELIVERED -> "تحویل شد"
    SmsDeliveryStatus.FAILED -> "تحویل نشد"
    SmsDeliveryStatus.PENDING -> "در انتظار تحویل"
    else -> status
}
