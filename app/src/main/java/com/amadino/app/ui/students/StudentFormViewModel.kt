package com.amadino.app.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StudentFormViewModel(
    private val studentRepository: StudentRepository,
    private val studentId: Int?
) : ViewModel() {

    enum class Field { FULL_NAME, FATHER_NAME, NATIONAL_ID, GRADE, MOBILE1, MOBILE2, TUITION, CURRENT_DEBT }

    data class UiState(
        val isNew: Boolean = false,
        val fullName: String = "",
        val fatherName: String = "",
        val nationalId: String = "",
        val grade: String = "",
        val mobile1: String = "",
        val mobile2: String = "",
        val totalTuition: String = "",
        val currentDebt: String = "",
        val isActive: Boolean = true,
        val errors: Map<Field, String> = emptyMap(),
        val saved: Boolean = false
    )

    private val _state = MutableStateFlow(UiState(isNew = studentId == null))
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var existing: StudentEntity? = null

    init {
        if (studentId != null) {
            viewModelScope.launch {
                studentRepository.getStudentById(studentId).first()?.let { student ->
                    existing = student
                    _state.value = UiState(
                        isNew = false,
                        fullName = student.fullName,
                        fatherName = student.fatherName,
                        nationalId = student.nationalId.orEmpty(),
                        grade = student.grade,
                        mobile1 = student.mobile1,
                        mobile2 = student.mobile2.orEmpty(),
                        totalTuition = student.totalTuition.toString(),
                        currentDebt = student.currentDebt.toString(),
                        isActive = student.isActive
                    )
                }
            }
        }
    }

    fun onValueChange(field: Field, value: String) {
        _state.value = when (field) {
            Field.FULL_NAME -> _state.value.copy(fullName = value, errors = _state.value.errors - field)
            Field.FATHER_NAME -> _state.value.copy(fatherName = value, errors = _state.value.errors - field)
            Field.NATIONAL_ID -> _state.value.copy(nationalId = value, errors = _state.value.errors - field)
            Field.GRADE -> _state.value.copy(grade = value, errors = _state.value.errors - field)
            Field.MOBILE1 -> _state.value.copy(mobile1 = value, errors = _state.value.errors - field)
            Field.MOBILE2 -> _state.value.copy(mobile2 = value, errors = _state.value.errors - field)
            Field.TUITION -> _state.value.copy(totalTuition = value, errors = _state.value.errors - field)
            Field.CURRENT_DEBT -> _state.value.copy(currentDebt = value, errors = _state.value.errors - field)
        }
    }

    fun onActiveChange(isActive: Boolean) {
        _state.value = _state.value.copy(isActive = isActive)
    }

    fun save() {
        val current = _state.value
        val errors = validate(current)
        if (errors.isNotEmpty()) {
            _state.value = current.copy(errors = errors)
            return
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val tuition = current.totalTuition.toAmount()
            val debt = current.currentDebt.toAmount().takeIf { !current.isNew } ?: tuition
            val entity = StudentEntity(
                id = existing?.id ?: 0,
                fullName = current.fullName.trim(),
                fatherName = current.fatherName.trim(),
                nationalId = current.nationalId.trim().ifBlank { null },
                grade = current.grade.trim(),
                mobile1 = current.mobile1.normalizeDigits(),
                mobile2 = current.mobile2.trim().normalizeDigits().ifBlank { null },
                totalTuition = tuition,
                currentDebt = debt,
                isActive = current.isActive,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now
            )

            if (existing == null) {
                studentRepository.insertStudent(entity)
            } else {
                studentRepository.updateStudent(entity)
            }
            _state.value = _state.value.copy(saved = true)
        }
    }

    private fun validate(state: UiState): Map<Field, String> {
        val errors = mutableMapOf<Field, String>()
        if (state.fullName.isBlank()) errors[Field.FULL_NAME] = "نام الزامی است."
        if (state.fatherName.isBlank()) errors[Field.FATHER_NAME] = "نام پدر الزامی است."
        if (state.grade.isBlank()) errors[Field.GRADE] = "پایه الزامی است."

        val mobile1 = state.mobile1.normalizeDigits()
        if (mobile1.isBlank()) {
            errors[Field.MOBILE1] = "شماره موبایل اول الزامی است."
        } else if (!MOBILE_PATTERN.matches(mobile1)) {
            errors[Field.MOBILE1] = "شماره موبایل معتبر نیست (مثال: 09123456789)."
        }

        val mobile2 = state.mobile2.normalizeDigits()
        if (mobile2.isNotBlank() && !MOBILE_PATTERN.matches(mobile2)) {
            errors[Field.MOBILE2] = "شماره موبایل دوم معتبر نیست."
        }

        val tuition = state.totalTuition.toAmount()
        if (tuition < 0) errors[Field.TUITION] = "شهریه نمی‌تواند منفی باشد."

        val debt = state.currentDebt.toAmount()
        if (!state.isNew && debt > tuition) {
            errors[Field.TUITION] = "بدهی نمی‌تواند از شهریه بیشتر باشد."
        }

        return errors
    }

    private fun String.normalizeDigits(): String = buildString(length) {
        for (char in this@normalizeDigits) {
            append(
                when (char) {
                    in '۰'..'۹' -> '0' + (char - '۰')
                    in '٠'..'٩' -> '0' + (char - '٠')
                    else -> char
                }
            )
        }
    }.replace(",", "").replace("٬", "").trim()

    private fun String.toAmount(): Long = normalizeDigits().toLongOrNull() ?: 0L

    private companion object {
        val MOBILE_PATTERN = Regex("^09\\d{9}$")
    }
}
