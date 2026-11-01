package com.amadino.app.data.sms

import com.amadino.app.domain.model.SmsEventType

/**
 * Persian SMS templates used for attendance notifications.
 *
 * Supported placeholders: `{student}`, `{date}`, `{time}`. The defaults are
 * intentionally short to stay within a single 70-character SMS part where
 * possible. Users can override each template in settings; the overrides live in
 * `sms_template` and fall back to the constants below when absent.
 */
object SmsTemplates {

    const val DEFAULT_ENTRY =
        "{student} عزیز، ورود شما در {date} ساعت {time} ثبت شد. آمادینو"

    const val DEFAULT_EXIT =
        "{student} عزیز، خروج شما در {date} ساعت {time} ثبت شد. آمادینو"

    /** Placeholders offered to the user when editing a template. */
    val PLACEHOLDERS = listOf("{student}", "{date}", "{time}")

    fun defaultTemplate(eventType: SmsEventType): String = when (eventType) {
        SmsEventType.ENTRY -> DEFAULT_ENTRY
        SmsEventType.EXIT -> DEFAULT_EXIT
    }

    /** Returns [stored] when it is non-blank, otherwise the built-in default. */
    fun resolveTemplate(eventType: SmsEventType, stored: String?): String =
        stored?.takeIf { it.isNotBlank() } ?: defaultTemplate(eventType)

    fun render(
        template: String,
        studentName: String,
        date: String,
        time: String
    ): String = template
        .replace("{student}", studentName)
        .replace("{date}", date)
        .replace("{time}", time)
}
