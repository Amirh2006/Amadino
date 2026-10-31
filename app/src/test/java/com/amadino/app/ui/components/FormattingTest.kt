package com.amadino.app.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattingTest {

    @Test
    fun `string digits are converted to persian`() {
        assertEquals("۰۹۱۲۳۴۵۶۷۸۹", "09123456789".toPersianDigits())
    }

    @Test
    fun `non digit characters are preserved`() {
        assertEquals("شماره ۱۲۳", "شماره 123".toPersianDigits())
    }

    @Test
    fun `toman amounts use persian digits`() {
        assertEquals("۵۰۰۰۰۰", formatToman(500_000L))
    }

    @Test
    fun `minutes of day are formatted as HH mm`() {
        assertEquals("۰۰:۰۰", formatMinutesOfDay(0))
        assertEquals("۰۹:۳۰", formatMinutesOfDay(570))
        assertEquals("۲۳:۵۹", formatMinutesOfDay(1_439))
    }

    @Test
    fun `minutes wrap around midnight`() {
        assertEquals("۰۰:۰۰", formatMinutesOfDay(1_440))
        assertEquals("۲۳:۵۹", formatMinutesOfDay(-1))
    }

    @Test
    fun `time range joins both ends`() {
        assertEquals("۰۸:۰۰ – ۰۹:۳۰", formatTimeRange(480, 570))
    }
}
