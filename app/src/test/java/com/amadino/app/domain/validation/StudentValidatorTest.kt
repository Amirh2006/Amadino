package com.amadino.app.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StudentValidatorTest {

    private fun validInput() = StudentValidator.Input(
        isNew = true,
        fullName = "علی رضایی",
        fatherName = "محمد",
        grade = "دهم",
        mobile1 = "09123456789",
        totalTuition = "5000000"
    )

    @Test
    fun `valid input has no errors`() {
        assertTrue(StudentValidator.validate(validInput()).isEmpty())
    }

    @Test
    fun `blank identity fields are rejected`() {
        val errors = StudentValidator.validate(
            validInput().copy(fullName = " ", fatherName = "", grade = "")
        )

        assertEquals(
            setOf(
                StudentValidator.Field.FULL_NAME,
                StudentValidator.Field.FATHER_NAME,
                StudentValidator.Field.GRADE
            ),
            errors.keys
        )
    }

    @Test
    fun `missing mobile is rejected`() {
        val errors = StudentValidator.validate(validInput().copy(mobile1 = ""))
        assertTrue(errors.containsKey(StudentValidator.Field.MOBILE1))
    }

    @Test
    fun `invalid mobile format is rejected`() {
        val errors = StudentValidator.validate(validInput().copy(mobile1 = "0912345"))
        assertTrue(errors.containsKey(StudentValidator.Field.MOBILE1))
    }

    @Test
    fun `persian digits are accepted for mobile`() {
        val errors = StudentValidator.validate(validInput().copy(mobile1 = "۰۹۱۲۳۴۵۶۷۸۹"))
        assertTrue(errors.isEmpty())
    }

    @Test
    fun `optional second mobile must be valid when present`() {
        assertTrue(
            StudentValidator.validate(validInput().copy(mobile2 = "09121112233")).isEmpty()
        )
        assertTrue(
            StudentValidator.validate(validInput().copy(mobile2 = "123"))
                .containsKey(StudentValidator.Field.MOBILE2)
        )
    }

    @Test
    fun `debt greater than tuition is rejected only when editing`() {
        val editing = validInput().copy(isNew = false, currentDebt = "6000000")
        assertTrue(
            StudentValidator.validate(editing).containsKey(StudentValidator.Field.TUITION)
        )

        val creating = validInput().copy(isNew = true, currentDebt = "6000000")
        assertTrue(StudentValidator.validate(creating).isEmpty())
    }

    @Test
    fun `normalizeDigits converts persian and arabic digits`() {
        assertEquals("09123456789", StudentValidator.normalizeDigits("۰۹۱۲۳۴۵۶۷۸۹"))
        assertEquals("09123456789", StudentValidator.normalizeDigits("٠٩١٢٣٤٥٦٧٨٩"))
    }

    @Test
    fun `amount strips separators and defaults to zero`() {
        assertEquals(5_000_000L, StudentValidator.amount("۵,۰۰۰,۰۰۰"))
        assertEquals(5_000_000L, StudentValidator.amount("5,000,000"))
        assertEquals(0L, StudentValidator.amount("abc"))
    }
}
