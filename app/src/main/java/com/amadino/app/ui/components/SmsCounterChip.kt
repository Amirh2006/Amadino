package com.amadino.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amadino.app.data.local.entity.SmsCounterEntity

/** Compact persistent indicator of how many SMS messages have been processed. */
@Composable
fun SmsCounterChip(
    counter: SmsCounterEntity?,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = colors.secondaryContainer,
        contentColor = colors.onSecondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "پیامک: ${(counter?.totalSent ?: 0L).toPersianDigits()}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "ناموفق ${(counter?.totalFailed ?: 0L).toPersianDigits()} · رد شده ${(counter?.totalSkipped ?: 0L).toPersianDigits()}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
