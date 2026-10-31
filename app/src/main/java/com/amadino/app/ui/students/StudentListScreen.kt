package com.amadino.app.ui.students

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
import androidx.compose.material3.OutlinedTextField
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
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.ui.components.EmptyState
import com.amadino.app.ui.components.StatusChip
import com.amadino.app.ui.components.formatToman
import com.amadino.app.ui.components.toPersianDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    viewModel: StudentListViewModel,
    onBack: () -> Unit,
    onAddStudent: () -> Unit,
    onEditStudent: (studentId: Int) -> Unit
) {
    val students by viewModel.students.collectAsState()
    val query by viewModel.query.collectAsState()
    var pendingDelete by remember { mutableStateOf<StudentEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "دانش‌آموزان") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "بازگشت") }
                },
                actions = {
                    TextButton(onClick = onAddStudent) { Text(text = "افزودن") }
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
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                label = { Text(text = "جستجو بر اساس نام یا پایه") },
                singleLine = true
            )

            if (students.isEmpty()) {
                EmptyState(
                    title = "دانش‌آموزی یافت نشد",
                    message = "با دکمه «افزودن» اولین دانش‌آموز را ثبت کنید."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = students, key = { it.id }) { student ->
                        StudentRow(
                            student = student,
                            onClick = { onEditStudent(student.id) },
                            onDelete = { pendingDelete = student }
                        )
                    }
                }
            }
        }
    }

    pendingDelete?.let { student ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(text = "حذف دانش‌آموز") },
            text = { Text(text = "آیا «${student.fullName}» حذف شود؟ سوابق حضور و پیامک نیز حذف می‌شوند.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.delete(student)
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

@Composable
private fun StudentRow(
    student: StudentEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "پایه ${student.grade} · ${student.mobile1.toPersianDigits()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (student.currentDebt > 0) {
                StatusChip(
                    label = "${formatToman(student.currentDebt)} بدهکار",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            } else {
                StatusChip(
                    label = "تسویه",
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            TextButton(onClick = onDelete) { Text(text = "حذف") }
        }
    }
}
