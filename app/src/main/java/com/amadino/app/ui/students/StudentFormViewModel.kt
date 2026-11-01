package com.amadino.app.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.StudentRepository
import com.amadino.app.domain.validation.StudentValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

typealias Field = StudentValidator.Field

class StudentFormViewModel(
    private val studentRepository: StudentRepository,
    private val studentId: Int?
) : ViewModel() {

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
            val tuition = StudentValidator.amount(current.totalTuition)
            val debt = if (current.isNew) {
                tuition
            } else {
                StudentValidator.amount(current.currentDebt)
            }
            val entity = StudentEntity(
                id = existing?.id ?: 0,
                fullName = current.fullName.trim(),
                fatherName = current.fatherName.trim(),
                nationalId = current.nationalId.trim().ifBlank { null },
                grade = current.grade.trim(),
                mobile1 = StudentValidator.normalizeDigits(current.mobile1),
                mobile2 = StudentValidator.normalizeDigits(current.mobile2.trim()).ifBlank { null },
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

    private fun validate(state: UiState): Map<Field, String> = StudentValidator.validate(
        StudentValidator.Input(
            isNew = state.isNew,
            fullName = state.fullName,
            fatherName = state.fatherName,
            grade = state.grade,
            mobile1 = state.mobile1,
            mobile2 = state.mobile2,
            totalTuition = state.totalTuition,
            currentDebt = state.currentDebt
        )
    )
}
