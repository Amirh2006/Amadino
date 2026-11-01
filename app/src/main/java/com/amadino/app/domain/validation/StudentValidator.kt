package com.amadino.app.domain.validation

/**
 * Pure validation for the student profile form, extracted from the ViewModel
 * so it can be unit-tested without Android or Room dependencies.
 */
object StudentValidator {

    enum class Field {
        FULL_NAME,
        FATHER_NAME,
        NATIONAL_ID,
        GRADE,
        MOBILE1,
        MOBILE2,
        TUITION,
        CURRENT_DEBT
    }

    data class Input(
        val isNew: Boolean,
        val fullName: String,
        val fatherName: String,
        val grade: String,
        val mobile1: String,
        val mobile2: String = "",
        val totalTuition: String = "",
        val currentDebt: String = ""
    )

    private val MOBILE_PATTERN = Regex("^09\\d{9}$")

    fun validate(input: Input): Map<Field, String> {
        val errors = mutableMapOf<Field, String>()

        if (input.fullName.isBlank()) errors[Field.FULL_NAME] = "نام الزامی است."
        if (input.fatherName.isBlank()) errors[Field.FATHER_NAME] = "نام پدر الزامی است."
        if (input.grade.isBlank()) errors[Field.GRADE] = "پایه الزامی است."

        val mobile1 = normalizeDigits(input.mobile1)
        if (mobile1.isBlank()) {
            errors[Field.MOBILE1] = "شماره موبایل اول الزامی است."
        } else if (!MOBILE_PATTERN.matches(mobile1)) {
            errors[Field.MOBILE1] = "شماره موبایل معتبر نیست (مثال: 09123456789)."
        }

        val mobile2 = normalizeDigits(input.mobile2)
        if (mobile2.isNotBlank() && !MOBILE_PATTERN.matches(mobile2)) {
            errors[Field.MOBILE2] = "شماره موبایل دوم معتبر نیست."
        }

        val tuition = amount(input.totalTuition)
        if (tuition < 0) errors[Field.TUITION] = "شهریه نمی‌تواند منفی باشد."

        val debt = amount(input.currentDebt)
        if (!input.isNew && debt > tuition) {
            errors[Field.TUITION] = "بدهی نمی‌تواند از شهریه بیشتر باشد."
        }

        return errors
    }

    /** Converts Persian/Arabic-Indic digits to ASCII and strips separators. */
    fun normalizeDigits(value: String): String = buildString(value.length) {
        for (char in value) {
            append(
                when (char) {
                    in '۰'..'۹' -> '0' + (char - '۰')
                    in '٠'..'٩' -> '0' + (char - '٠')
                    else -> char
                }
            )
        }
    }.replace(",", "").replace("٬", "").trim()

    /** Parses a Toman amount, treating unparsable input as zero. */
    fun amount(value: String): Long = normalizeDigits(value).toLongOrNull() ?: 0L
}
