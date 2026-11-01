package com.amadino.app.ui.attendance

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
import androidx.compose.material3.Button
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
import com.amadino.app.data.sms.hasSmsPermission
import com.amadino.app.domain.model.SmsEventType
import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.formatTimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val smsPrompt by viewModel.smsPrompt.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var pendingEvent by remember { mutableStateOf<SmsEventType?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val event = pendingEvent
        pendingEvent = null
        if (granted && event != null) {
            viewModel.sendSms(event)
        }
    }

    val sendWithPermission: (SmsEventType) -> Unit = { event ->
        if (context.hasSmsPermission()) {
            viewModel.sendSms(event)
        } else {
            pendingEvent = event
            permissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.classTime?.name ?: "ثبت حضور",
                            style = MaterialTheme.typography.titleMedium
                        )
                        val subtitle = listOfNotNull(
                            state.shift?.name,
                            state.classTime?.let { formatTimeRange(it.startTime, it.endTime) }
                        ).joinToString(" · ")
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "بازگشت") }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { viewModel.markAll(AttendanceViewModel.Status.PRESENT) }) {
                            Text(text = "همه حاضر")
                        }
                        TextButton(onClick = { viewModel.markAll(AttendanceViewModel.Status.ABSENT) }) {
                            Text(text = "همه غایب")
                        }
                        Button(
                            onClick = viewModel::save,
                            modifier = Modifier.weight(1f),
                            enabled = state.pendingCount > 0
                        ) {
                            Text(text = "ذخیره (${state.pendingCount.toPersianDigits()})")
                        }
                    }
                    TextButton(
                        onClick = viewModel::requestExitSms,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "ارسال پیامک خروج")
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.rows.isEmpty()) {
            EmptyState(
                title = "دانش‌آموز فعالی وجود ندارد",
                message = "برای ثبت حضور ابتدا دانش‌آموز فعال ثبت کنید.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = state.rows, key = { it.student.id }) { row ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = row.student.fullName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "پایه ${row.student.grade}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                AttendanceViewModel.Status.entries.forEach { status ->
                                    StatusOption(
                                        label = status.label(),
                                        selected = row.status == status.value,
                                        onClick = { viewModel.setStatus(row.student.id, status) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    smsPrompt?.let { prompt ->
        val isEntry = prompt.eventType == SmsEventType.ENTRY
        AlertDialog(
            onDismissRequest = viewModel::dismissSmsPrompt,
            title = { Text(text = if (isEntry) "ارسال پیامک ورود" else "ارسال پیامک خروج") },
            text = {
                Text(
                    text = "برای ${prompt.recipients.toPersianDigits()} دانش‌آموز پیامک ارسال شود؟"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.dismissSmsPrompt()
                        sendWithPermission(prompt.eventType)
                    }
                ) { Text(text = "ارسال") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissSmsPrompt) { Text(text = "بعداً") }
            }
        )
    }
}

@Composable
private fun StatusOption(
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

private fun AttendanceViewModel.Status.label(): String = when (this) {
    AttendanceViewModel.Status.PRESENT -> "حاضر"
    AttendanceViewModel.Status.LATE -> "تأخیر"
    AttendanceViewModel.Status.ABSENT -> "غایب"
}

private fun Int.toPersianDigits(): String =
    toString().map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")
