package com.amadino.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.components.TimeFields
import com.amadino.app.ui.settings.ClassTimeFormViewModel.Field

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassTimeFormScreen(
    viewModel: ClassTimeFormViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.isNew) "زنگ جدید" else "ویرایش زنگ") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "نام زنگ *") },
                isError = state.errors.containsKey(Field.NAME),
                singleLine = true
            )

            Text(text = "ساعت شروع", style = MaterialTheme.typography.titleSmall)
            TimeFields(
                hour = state.startHour,
                minute = state.startMinute,
                onHourChange = viewModel::onStartHourChange,
                onMinuteChange = viewModel::onStartMinuteChange
            )

            Text(text = "ساعت پایان", style = MaterialTheme.typography.titleSmall)
            TimeFields(
                hour = state.endHour,
                minute = state.endMinute,
                onHourChange = viewModel::onEndHourChange,
                onMinuteChange = viewModel::onEndMinuteChange
            )

            OutlinedTextField(
                value = state.sortOrder,
                onValueChange = viewModel::onSortOrderChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "ترتیب نمایش") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            state.errors[Field.TIME]?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text(text = "ذخیره")
            }
        }
    }
}
