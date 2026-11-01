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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.amadino.app.data.local.entity.WeekDayEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekDaySettingsScreen(
    viewModel: WeekDaySettingsViewModel,
    onBack: () -> Unit,
    onOpenDay: (dayId: Int) -> Unit
) {
    val days by viewModel.days.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "روزها و شیفت‌ها") },
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
            Text(
                text = "روزهای مدرسه را مشخص کنید. برای هر روز می‌توانید شیفت‌ها را مدیریت کنید.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = days, key = { it.id }) { day ->
                    WeekDayRow(
                        day = day,
                        onToggle = { viewModel.toggleSchoolDay(day) },
                        onOpen = { onOpenDay(day.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDayRow(
    day: WeekDayEntity,
    onToggle: () -> Unit,
    onOpen: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = day.name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = if (day.isSchoolDay) "روز مدرسه · مدیریت شیفت‌ها" else "تعطیل · مدیریت شیفت‌ها",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = day.isSchoolDay, onCheckedChange = { onToggle() })
        }
    }
}
