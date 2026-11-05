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
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
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
        // 1. Glowing Logo
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

        // 2. Live Digital Clock & Jalali Date
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

    // Convert the English time string to Persian digits
    val persianTime = toPersianDigits(currentTime)

    Text(
        text = persianTime,
        color = ElectricAqua,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp,
        style = TextStyle(textDirection = TextDirection.Ltr) // LTR to keep colon in correct place
    )
}

/**
 * Accurate Gregorian to Jalali (Shamsi) converter with Persian digits.
 */
fun getCurrentJalaliDate(): String {
    val calendar = Calendar.getInstance()
    val gy = calendar.get(Calendar.YEAR)
    val gm = calendar.get(Calendar.MONTH) + 1
    val gd = calendar.get(Calendar.DAY_OF_MONTH)

    val gDaysInMonth = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
    var jy = if (gy <= 1600) 0 else 979
    val gy2 = gy - (if (gy <= 1600) 621 else 1600)
    val gy3 = if (gm > 2) (gy2 + 1) else gy2

    var days = (365 * gy2) + ((gy3 + 3) / 4) - ((gy3 + 99) / 100) + ((gy3 + 399) / 400) - 80 + gd + gDaysInMonth[gm - 1]

    jy += 33 * (days / 12053)
    days %= 12053
    jy += 4 * (days / 1461)
    days %= 1461

    if (days > 365) {
        jy += (days - 1) / 365
        days = (days - 1) % 365
    }

    val jm = if (days < 186) 1 + (days / 31) else 7 + ((days - 186) / 30)
    val jd = 1 + (if (days < 186) (days % 31) else ((days - 186) % 30))

    val monthNames = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    val jdPersian = toPersianDigits(jd.toString())
    val jyPersian = toPersianDigits(jy.toString())

    // Adding \u200F (Right-to-Left Mark) to force RTL rendering
    return "\u200F$jdPersian ${monthNames[jm - 1]} $jyPersian"
}

/**
 * Helper function to convert English digits (0-9) to Persian digits (۰-۹).
 */
fun toPersianDigits(input: String): String {
    val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
    val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    val builder = StringBuilder()
    for (char in input) {
        val index = englishDigits.indexOf(char)
        if (index != -1) {
            builder.append(persianDigits[index])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}