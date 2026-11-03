package com.amadino.app.ui.components

import android.R.attr.bottom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Bottom row of shortcuts from the dashboard to the main sections. */
@Composable
fun QuickActionBar(
    onStudents: () -> Unit,
    onFinance: () -> Unit,
    onReports: () -> Unit,
    onSmsLog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuickAction(label = "دانش‌آموزان", onClick = onStudents)
        QuickAction(label = "مالی", onClick = onFinance)
        QuickAction(label = "گزارش‌ها", onClick = onReports)
        QuickAction(label = "پیامک", onClick = onSmsLog)
    }
}

@Composable
private fun QuickAction(
    label: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
