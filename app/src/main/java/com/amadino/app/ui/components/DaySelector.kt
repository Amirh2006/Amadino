package com.amadino.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.amadino.app.data.local.entity.WeekDayEntity

@Composable
fun DaySelector(
    days: List<WeekDayEntity>,
    selectedDayId: Int,
    onDaySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(items = days, key = { it.id }) { day ->
            DayChip(
                day = day,
                selected = day.id == selectedDayId,
                onClick = { onDaySelected(day.id) }
            )
        }
    }
}

@Composable
private fun DayChip(
    day: WeekDayEntity,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val background = when {
        selected -> colors.primary
        day.isSchoolDay -> colors.surfaceVariant
        else -> colors.surface
    }
    val content = when {
        selected -> colors.onPrimary
        day.isSchoolDay -> colors.onSurfaceVariant
        else -> colors.onSurface.copy(alpha = 0.38f)
    }

    Surface(
        modifier = modifier.clickable(enabled = day.isSchoolDay, onClick = onClick),
        shape = RoundedCornerShape(50),
        color = background,
        contentColor = content,
        tonalElevation = if (selected) 0.dp else 1.dp
    ) {
        Text(
            text = day.name,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
