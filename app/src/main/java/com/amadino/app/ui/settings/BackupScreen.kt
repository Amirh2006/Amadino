package com.amadino.app.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupScreen(
    viewModel: BackupViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var pendingImport by remember { mutableStateOf<Uri?>(null) }

    val createLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let(viewModel::export)
    }

    val openLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { pendingImport = it }
    }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "پشتیبان‌گیری") },
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
                text = "یک نسخه پشتیبان کامل از دانش‌آموزان، شیفت‌ها، حضور و غیاب، " +
                    "پرداخت‌ها، پیامک‌ها و قالب‌ها در یک فایل JSON ذخیره کنید. " +
                    "همه چیز روی همین دستگاه می‌ماند.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { createLauncher.launch(defaultFileName()) },
                enabled = !state.isBusy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "خروجی گرفتن (JSON)")
            }

            OutlinedButton(
                onClick = {
                    openLauncher.launch(
                        arrayOf("application/json", "text/plain", "application/octet-stream")
                    )
                },
                enabled = !state.isBusy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "بازیابی از فایل")
            }

            if (state.isBusy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Text(
                text = "بازیابی، اطلاعات فعلی را با محتوای فایل جایگزین می‌کند. " +
                    "پیش از بازیابی از نسخه پشتیبان فعلی مطمئن شوید.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    pendingImport?.let { uri ->
        AlertDialog(
            onDismissRequest = { pendingImport = null },
            title = { Text(text = "بازیابی اطلاعات") },
            text = {
                Text(
                    text = "همه اطلاعات فعلی با محتوای فایل انتخاب‌شده جایگزین می‌شود. " +
                        "ادامه می‌دهید؟"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.import(uri)
                        pendingImport = null
                    }
                ) { Text(text = "بازیابی") }
            },
            dismissButton = {
                TextButton(onClick = { pendingImport = null }) { Text(text = "انصراف") }
            }
        )
    }
}

private fun defaultFileName(): String {
    val stamp = SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())
    return "amadino-backup-$stamp.json"
}
