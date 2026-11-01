package com.amadino.app.data.sms

import com.amadino.app.domain.model.SmsEventType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsTemplatesTest {

    @Test
    fun `render replaces all placeholders`() {
        val rendered = SmsTemplates.render(
            template = "{student} در {date} ساعت {time}",
            studentName = "علی",
            date = "1405/08/10",
            time = "08:15"
        )

        assertEquals("علی در 1405/08/10 ساعت 08:15", rendered)
    }

    @Test
    fun `default templates contain every placeholder`() {
        listOf(SmsTemplates.DEFAULT_ENTRY, SmsTemplates.DEFAULT_EXIT).forEach { template ->
            assertTrue(template.contains("{student}"))
            assertTrue(template.contains("{date}"))
            assertTrue(template.contains("{time}"))
        }
    }

    @Test
    fun `unknown placeholders are left untouched`() {
        val rendered = SmsTemplates.render(
            template = "{student} {unknown}",
            studentName = "سارا",
            date = "1405/08/10",
            time = "09:00"
        )

        assertEquals("سارا {unknown}", rendered)
    }

    @Test
    fun `stored template overrides the default`() {
        assertEquals(
            "{student} سلام",
            SmsTemplates.resolveTemplate(SmsEventType.ENTRY, "{student} سلام")
        )
    }

    @Test
    fun `blank or missing template falls back to the default`() {
        assertEquals(
            SmsTemplates.DEFAULT_ENTRY,
            SmsTemplates.resolveTemplate(SmsEventType.ENTRY, "   ")
        )
        assertEquals(
            SmsTemplates.DEFAULT_EXIT,
            SmsTemplates.resolveTemplate(SmsEventType.EXIT, null)
        )
    }

    @Test
    fun `each event type has its own default`() {
        assertEquals(SmsTemplates.DEFAULT_ENTRY, SmsTemplates.defaultTemplate(SmsEventType.ENTRY))
        assertEquals(SmsTemplates.DEFAULT_EXIT, SmsTemplates.defaultTemplate(SmsEventType.EXIT))
    }

    @Test
    fun `documented placeholders match the supported tokens`() {
        assertEquals(listOf("{student}", "{date}", "{time}"), SmsTemplates.PLACEHOLDERS)
    }
}
