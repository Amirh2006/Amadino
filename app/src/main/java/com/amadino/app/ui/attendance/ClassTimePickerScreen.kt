package com.amadino.app.ui.attendance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.formatTimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassTimePickerScreen(
    viewModel: ClassTimePickerViewModel,
    onBack: () -> Unit,
    onSelectClassTime: (classTimeId: Int) -> Unit
) {
    val shift by viewModel.shift.collectAsState()
    val classTimes by viewModel.classTimes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = shift?.name ?: "انتخاب زنگ") },
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
            if (classTimes.isEmpty()) {
                EmptyState(
                    title = "زنگی تعریف نشده است",
                    message = "برای این شیفت هنوز زنگی ثبت نشده است."
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = classTimes, key = { it.id }) { classTime ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectClassTime(classTime.id) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = classTime.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = formatTimeRange(classTime.startTime, classTime.endTime),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
