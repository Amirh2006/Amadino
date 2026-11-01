package com.amadino.app.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScheduleValidatorTest {

    @Test
    fun `valid range within a day is accepted`() {
        assertTrue(
            ScheduleValidator.validateTimeRange(480, 570) is ScheduleValidator.TimeRangeResult.Valid
        )
    }

    @Test
    fun `end must be strictly after start`() {
        assertTrue(
            ScheduleValidator.validateTimeRange(570, 570) is ScheduleValidator.TimeRangeResult.Invalid
        )
        assertTrue(
            ScheduleValidator.validateTimeRange(600, 570) is ScheduleValidator.TimeRangeResult.Invalid
        )
    }

    @Test
    fun `out of day bounds is rejected`() {
        assertTrue(
            ScheduleValidator.validateTimeRange(-1, 60) is ScheduleValidator.TimeRangeResult.Invalid
        )
        assertTrue(
            ScheduleValidator.validateTimeRange(0, 1_440) is ScheduleValidator.TimeRangeResult.Invalid
        )
    }

    @Test
    fun `hour and minute parse into minutes of day`() {
        assertEquals(480L, ScheduleValidator.toMinutesOfDay("08", "00"))
        assertEquals(1_439L, ScheduleValidator.toMinutesOfDay("23", "59"))
        assertEquals(570L, ScheduleValidator.toMinutesOfDay("۰۹", "۳۰"))
    }

    @Test
    fun `invalid time text parses to null`() {
        assertNull(ScheduleValidator.toMinutesOfDay("24", "00"))
        assertNull(ScheduleValidator.toMinutesOfDay("08", "60"))
        assertNull(ScheduleValidator.toMinutesOfDay("", "00"))
        assertNull(ScheduleValidator.toMinutesOfDay("ab", "00"))
    }

    @Test
    fun `minutes split back into padded hour and minute`() {
        assertEquals("08" to "00", ScheduleValidator.fromMinutesOfDay(480))
        assertEquals("00" to "05", ScheduleValidator.fromMinutesOfDay(5))
        assertEquals("23" to "59", ScheduleValidator.fromMinutesOfDay(1_439))
    }

    @Test
    fun `minutes wrap around midnight when splitting`() {
        assertEquals("00" to "00", ScheduleValidator.fromMinutesOfDay(1_440))
        assertEquals("23" to "59", ScheduleValidator.fromMinutesOfDay(-1))
    }
}
