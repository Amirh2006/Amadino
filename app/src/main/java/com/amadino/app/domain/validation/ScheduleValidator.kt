package com.amadino.app.domain.validation

/**
 * Validation for shifts and class times. Times are stored as minutes since
 * midnight, so a range is valid when the end is strictly after the start and
 * both ends are inside a single day.
 */
object ScheduleValidator {

    const val MINUTES_PER_DAY = 1_440

    sealed interface TimeRangeResult {
        data object Valid : TimeRangeResult
        data class Invalid(val reason: String) : TimeRangeResult
    }

    fun validateTimeRange(startMinutes: Long, endMinutes: Long): TimeRangeResult {
        if (startMinutes !in 0 until MINUTES_PER_DAY.toLong()) {
            return TimeRangeResult.Invalid("ساعت شروع معتبر نیست.")
        }
        if (endMinutes !in 0 until MINUTES_PER_DAY.toLong()) {
            return TimeRangeResult.Invalid("ساعت پایان معتبر نیست.")
        }
        if (endMinutes <= startMinutes) {
            return TimeRangeResult.Invalid("ساعت پایان باید بعد از شروع باشد.")
        }
        return TimeRangeResult.Valid
    }

    /** Parses `HH`/`mm` text fields into minutes since midnight, or null. */
    fun toMinutesOfDay(hour: String, minute: String): Long? {
        val parsedHour = StudentValidator.normalizeDigits(hour).toIntOrNull() ?: return null
        val parsedMinute = StudentValidator.normalizeDigits(minute).toIntOrNull() ?: return null
        if (parsedHour !in 0..23 || parsedMinute !in 0..59) return null
        return (parsedHour * 60 + parsedMinute).toLong()
    }

    /** Splits stored minutes into `HH` and `mm` strings for form fields. */
    fun fromMinutesOfDay(minutes: Long): Pair<String, String> {
        val normalized = ((minutes % MINUTES_PER_DAY) + MINUTES_PER_DAY) % MINUTES_PER_DAY
        return (normalized / 60).toString().padStart(2, '0') to
            (normalized % 60).toString().padStart(2, '0')
    }
}
