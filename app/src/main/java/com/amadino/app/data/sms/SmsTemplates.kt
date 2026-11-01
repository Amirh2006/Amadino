package com.amadino.app.data.sms

/**
 * Persian SMS templates used for attendance notifications.
 *
 * Supported placeholders: `{student}`, `{date}`, `{time}`. The defaults are
 * intentionally short to stay within a single 70-character SMS part where
 * possible.
 */
object SmsTemplates {

    const val DEFAULT_ENTRY =
        "{student} عزیز، ورود شما در {date} ساعت {time} ثبت شد. آمادینو"

    const val DEFAULT_EXIT =
        "{student} عزیز، خروج شما در {date} ساعت {time} ثبت شد. آمادینو"

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
