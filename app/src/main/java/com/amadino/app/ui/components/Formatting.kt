package com.amadino.app.ui.components

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val PERSIAN_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

/** Converts every ASCII digit in the receiver to its Persian counterpart. */
fun String.toPersianDigits(): String = buildString(length) {
    for (char in this@toPersianDigits) {
        append(if (char in '0'..'9') PERSIAN_DIGITS[char - '0'] else char)
    }
}

fun Long.toPersianDigits(): String = toString().toPersianDigits()

/** Formats an integer Toman amount with Persian digits. */
fun formatToman(amount: Long): String = amount.toPersianDigits()

/**
 * Formats a time-of-day value stored as minutes since midnight (0..1439)
 * as `HH:mm` using Persian digits.
 */
fun formatMinutesOfDay(minutes: Long): String {
    val normalized = ((minutes % MINUTES_PER_DAY) + MINUTES_PER_DAY) % MINUTES_PER_DAY
    val hour = normalized / 60
    val minute = normalized % 60
    return "%02d:%02d".format(Locale.US, hour, minute).toPersianDigits()
}

/** Formats a range of two time-of-day values as `HH:mm – HH:mm`. */
fun formatTimeRange(startMinutes: Long, endMinutes: Long): String =
    "${formatMinutesOfDay(startMinutes)} – ${formatMinutesOfDay(endMinutes)}"

/** Formats an epoch-millis timestamp as `yyyy/MM/dd`. */
fun formatDate(millis: Long): String =
    SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(millis)).toPersianDigits()

/** Formats an epoch-millis timestamp as `yyyy/MM/dd HH:mm`. */
fun formatDateTime(millis: Long): String =
    SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US).format(Date(millis)).toPersianDigits()

private const val MINUTES_PER_DAY = 1_440L
