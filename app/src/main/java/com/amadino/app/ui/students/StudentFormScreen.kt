package com.amadino.app.ui.students

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.students.Field

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormScreen(
    viewModel: StudentFormViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saved) {
        if (state.saved) {
            snackbarHostState.showSnackbar("دانش‌آموز با موفقیت ثبت شد.")
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (state.isNew) "دانش‌آموز جدید" else "ویرایش دانش‌آموز") },
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
            Text(text = "مشخصات هویتی", style = MaterialTheme.typography.titleSmall)

            FormField(
                value = state.fullName,
                onValueChange = { viewModel.onValueChange(Field.FULL_NAME, it) },
                label = "نام و نام خانوادگی *",
                error = state.errors[Field.FULL_NAME]
            )
            FormField(
                value = state.fatherName,
                onValueChange = { viewModel.onValueChange(Field.FATHER_NAME, it) },
                label = "نام پدر *",
                error = state.errors[Field.FATHER_NAME]
            )
            FormField(
                value = state.nationalId,
                onValueChange = { viewModel.onValueChange(Field.NATIONAL_ID, it) },
                label = "کد ملی",
                error = state.errors[Field.NATIONAL_ID],
                keyboardType = KeyboardType.Number
            )
            FormField(
                value = state.grade,
                onValueChange = { viewModel.onValueChange(Field.GRADE, it) },
                label = "پایه *",
                error = state.errors[Field.GRADE]
            )

            Text(text = "اطلاعات تماس", style = MaterialTheme.typography.titleSmall)

            FormField(
                value = state.mobile1,
                onValueChange = { viewModel.onValueChange(Field.MOBILE1, it) },
                label = "موبایل اول (اصلی) *",
                error = state.errors[Field.MOBILE1],
                keyboardType = KeyboardType.Phone
            )
            FormField(
                value = state.mobile2,
                onValueChange = { viewModel.onValueChange(Field.MOBILE2, it) },
                label = "موبایل دوم (جانشین)",
                error = state.errors[Field.MOBILE2],
                keyboardType = KeyboardType.Phone
            )

            Text(text = "اطلاعات مالی", style = MaterialTheme.typography.titleSmall)

            FormField(
                value = state.totalTuition,
                onValueChange = { viewModel.onValueChange(Field.TUITION, it) },
                label = "شهریه کل (تومان) *",
                error = state.errors[Field.TUITION],
                keyboardType = KeyboardType.Number
            )

            if (!state.isNew) {
                FormField(
                    value = state.currentDebt,
                    onValueChange = { viewModel.onValueChange(Field.CURRENT_DEBT, it) },
                    label = "بدهی فعلی (تومان)",
                    error = null,
                    keyboardType = KeyboardType.Number
                )
            } else {
                Text(
                    text = "بدهی فعلی در زمان ثبت برابر شهریه کل تنظیم می‌شود.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "دانش‌آموز فعال", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = state.isActive, onCheckedChange = viewModel::onActiveChange)
            }

            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "ذخیره")
            }
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = label) },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            )
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}
