package com.amadino.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.components.TimeFields
import com.amadino.app.ui.settings.ShiftFormViewModel.Field

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftFormScreen(
    viewModel: ShiftFormViewModel,
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
                title = { Text(text = if (state.isNew) "شیفت جدید" else "ویرایش شیفت") },
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
                label = { Text(text = "نام شیفت *") },
                isError = state.errors.containsKey(Field.NAME),
                singleLine = true
            )

            OutlinedTextField(
                value = state.monitorName,
                onValueChange = viewModel::onMonitorChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "نام مسئول") },
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

            state.errors[Field.TIME]?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "شیفت فعال", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = state.isActive, onCheckedChange = viewModel::onActiveChange)
            }

            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text(text = "ذخیره")
            }
        }
    }
}
