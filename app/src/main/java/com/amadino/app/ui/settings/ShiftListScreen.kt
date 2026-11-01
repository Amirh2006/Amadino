package com.amadino.app.ui.settings

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.formatTimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftListScreen(
    viewModel: ShiftListViewModel,
    onBack: () -> Unit,
    onAddShift: () -> Unit,
    onEditShift: (shiftId: Int) -> Unit,
    onOpenClassTimes: (shiftId: Int) -> Unit
) {
    val day by viewModel.day.collectAsState()
    val shifts by viewModel.shifts.collectAsState()
    var pendingDelete by remember { mutableStateOf<ShiftEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = day?.name ?: "شیفت‌ها") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "بازگشت") }
                },
                actions = {
                    TextButton(onClick = onAddShift) { Text(text = "افزودن") }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        if (shifts.isEmpty()) {
            EmptyState(
                title = "شیفتی ثبت نشده است",
                message = "برای این روز اولین شیفت را با دکمه «افزودن» بسازید.",
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
                items(items = shifts, key = { it.id }) { shift ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditShift(shift.id) }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = shift.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (shift.isActive) "فعال" else "غیرفعال",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (shift.isActive) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                            Text(
                                text = formatTimeRange(shift.startTime, shift.endTime),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "مسئول: ${shift.monitorName.ifBlank { "ثبت نشده" }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { onOpenClassTimes(shift.id) }) {
                                    Text(text = "زنگ‌ها")
                                }
                                TextButton(onClick = { pendingDelete = shift }) {
                                    Text(text = "حذف")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { shift ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(text = "حذف شیفت") },
            text = { Text(text = "شیفت «${shift.name}» و زنگ‌های آن حذف شود؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(shift)
                        pendingDelete = null
                    }
                ) { Text(text = "حذف") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(text = "انصراف") }
            }
        )
    }
}
