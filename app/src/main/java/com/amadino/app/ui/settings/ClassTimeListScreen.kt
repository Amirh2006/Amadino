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
import androidx.compose.ui.unit.dp
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.formatTimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassTimeListScreen(
    viewModel: ClassTimeListViewModel,
    onBack: () -> Unit,
    onAddClassTime: () -> Unit,
    onEditClassTime: (classTimeId: Int) -> Unit
) {
    val shift by viewModel.shift.collectAsState()
    val classTimes by viewModel.classTimes.collectAsState()
    var pendingDelete by remember { mutableStateOf<ClassTimeEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = shift?.name ?: "زنگ‌ها") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "بازگشت") }
                },
                actions = {
                    TextButton(onClick = onAddClassTime) { Text(text = "افزودن") }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { innerPadding ->
        if (classTimes.isEmpty()) {
            EmptyState(
                title = "زنگی ثبت نشده است",
                message = "برای این شیفت اولین زنگ را با دکمه «افزودن» بسازید.",
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
                items(items = classTimes, key = { it.id }) { classTime ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditClassTime(classTime.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = classTime.name,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = formatTimeRange(classTime.startTime, classTime.endTime),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TextButton(onClick = { pendingDelete = classTime }) {
                                Text(text = "حذف")
                            }
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { classTime ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(text = "حذف زنگ") },
            text = { Text(text = "زنگ «${classTime.name}» حذف شود؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(classTime)
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
