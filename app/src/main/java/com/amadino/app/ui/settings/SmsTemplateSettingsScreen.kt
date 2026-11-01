package com.amadino.app.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.amadino.app.data.sms.SmsTemplates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsTemplateSettingsScreen(
    viewModel: SmsTemplateSettingsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var confirmReset by remember { mutableStateOf(false) }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar("قالب‌ها ذخیره شد.")
            viewModel.consumeSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "قالب پیامک‌ها") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text(text = "بازگشت") }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "می‌توانید متن پیامک ورود و خروج را تغییر دهید. " +
                    "جای‌نگهدارها هنگام ارسال جایگزین می‌شوند: " +
                    SmsTemplates.PLACEHOLDERS.joinToString("، "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = state.entry,
                onValueChange = viewModel::onEntryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "پیامک ورود") },
                isError = state.entryError != null,
                minLines = 3,
                supportingText = {
                    Text(text = state.entryError ?: state.entryPreview)
                }
            )

            OutlinedTextField(
                value = state.exit,
                onValueChange = viewModel::onExitChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "پیامک خروج") },
                isError = state.exitError != null,
                minLines = 3,
                supportingText = {
                    Text(text = state.exitError ?: state.exitPreview)
                }
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "پیش‌نمایش",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = state.entryPreview,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = state.exitPreview,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Button(
                onClick = viewModel::save,
                enabled = state.isLoaded,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "ذخیره")
            }

            TextButton(
                onClick = { confirmReset = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "بازگردانی به قالب پیش‌فرض")
            }
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text(text = "بازگردانی قالب‌ها") },
            text = { Text(text = "متن‌های سفارشی حذف و قالب‌های پیش‌فرض بازگردانی شوند؟") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        confirmReset = false
                    }
                ) { Text(text = "بازگردانی") }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text(text = "انصراف") }
            }
        )
    }
}
