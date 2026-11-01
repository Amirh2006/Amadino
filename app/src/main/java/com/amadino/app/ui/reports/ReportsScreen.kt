package com.amadino.app.ui.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amadino.app.ui.components.formatToman

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel,
    onBack: () -> Unit
) {
    val summary by viewModel.summary.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "گزارش‌ها") },
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
            ReportCard(title = "دانش‌آموزان") {
                ReportRow(label = "کل دانش‌آموزان", value = summary.totalStudents.toPersian())
                ReportRow(label = "فعال", value = summary.activeStudents.toPersian())
            }

            ReportCard(title = "حضور امروز") {
                ReportRow(label = "حاضر", value = summary.todayPresent.toPersian())
                ReportRow(label = "تأخیر", value = summary.todayLate.toPersian())
                ReportRow(label = "غایب", value = summary.todayAbsent.toPersian())
            }

            ReportCard(title = "مالی") {
                ReportRow(label = "مجموع بدهی", value = "${formatToman(summary.totalDebt)} تومان")
                ReportRow(label = "مجموع پرداخت‌ها", value = "${formatToman(summary.totalPayments)} تومان")
            }

            ReportCard(title = "پیامک") {
                ReportRow(label = "ارسال موفق", value = summary.smsSent.toPersian())
                ReportRow(label = "ناموفق", value = summary.smsFailed.toPersian())
                ReportRow(label = "رد شده", value = summary.smsSkipped.toPersian())
            }
        }
    }
}

@Composable
private fun ReportCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

private fun Int.toPersian(): String =
    toString().map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")

private fun Long.toPersian(): String =
    toString().map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")
