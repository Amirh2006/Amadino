package com.amadino.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amadino.app.R
import com.amadino.app.ui.theme.CyberPurple
import com.amadino.app.ui.theme.ElectricAqua
import com.amadino.app.ui.theme.SoftLilac
import com.amadino.app.utils.getCurrentJalaliDate
import com.amadino.app.utils.toPersianDigits
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BrandHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(CyberPurple.copy(alpha = 0.6f), Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Amadino Brand Logo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            LiveDigitalClock()
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getCurrentJalaliDate(),
                color = SoftLilac,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                style = TextStyle(textDirection = TextDirection.Rtl)
            )
        }
    }
}

@Composable
fun LiveDigitalClock() {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        while (true) {
            currentTime = formatter.format(Date())
            delay(1000L)
        }
    }

    Text(
        text = toPersianDigits(currentTime),
        color = ElectricAqua,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        style = TextStyle(textDirection = TextDirection.Ltr)
    )
}