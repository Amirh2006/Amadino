package com.amadino.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Small pill used for statuses such as SMS delivery results or attendance states. */
@Composable
fun StatusChip(
    label: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
fun SmsStatusChip(status: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (status.uppercase()) {
        "SUCCESS" -> colors.secondaryContainer to colors.onSecondaryContainer
        "FAILED" -> colors.errorContainer to colors.onErrorContainer
        "PENDING" -> colors.tertiaryContainer to colors.onTertiaryContainer
        else -> colors.surfaceVariant to colors.onSurfaceVariant // SKIPPED
    }
    StatusChip(label = status, containerColor = container, contentColor = content, modifier = modifier)
}

@Composable
fun AttendanceStatusChip(status: String, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val (container, content) = when (status.uppercase()) {
        "PRESENT" -> colors.secondaryContainer to colors.onSecondaryContainer
        "LATE" -> colors.tertiaryContainer to colors.onTertiaryContainer
        else -> colors.errorContainer to colors.onErrorContainer // ABSENT
    }
    StatusChip(label = status, containerColor = container, contentColor = content, modifier = modifier)
}
