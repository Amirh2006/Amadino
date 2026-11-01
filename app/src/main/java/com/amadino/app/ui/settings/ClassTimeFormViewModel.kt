package com.amadino.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.repository.ShiftRepository
import com.amadino.app.domain.validation.ScheduleValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ClassTimeFormViewModel(
    private val shiftRepository: ShiftRepository,
    private val shiftId: Int,
    private val classTimeId: Int?
) : ViewModel() {

    enum class Field { NAME, TIME }

    data class UiState(
        val isNew: Boolean = false,
        val name: String = "",
        val startHour: String = "08",
        val startMinute: String = "00",
        val endHour: String = "09",
        val endMinute: String = "30",
        val sortOrder: String = "0",
        val errors: Map<Field, String> = emptyMap(),
        val saved: Boolean = false
    )

    private val _state = MutableStateFlow(UiState(isNew = classTimeId == null))
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var existing: ClassTimeEntity? = null

    init {
        if (classTimeId != null) {
            viewModelScope.launch {
                shiftRepository.getClassTimesByShift(shiftId).first()
                    .firstOrNull { it.id == classTimeId }
                    ?.let { classTime ->
                        existing = classTime
                        val (startHour, startMinute) =
                            ScheduleValidator.fromMinutesOfDay(classTime.startTime)
                        val (endHour, endMinute) =
                            ScheduleValidator.fromMinutesOfDay(classTime.endTime)
                        _state.value = UiState(
                            isNew = false,
                            name = classTime.name,
                            startHour = startHour,
                            startMinute = startMinute,
                            endHour = endHour,
                            endMinute = endMinute,
                            sortOrder = classTime.sortOrder.toString()
                        )
                    }
            }
        }
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, errors = _state.value.errors - Field.NAME)
    }

    fun onStartHourChange(value: String) {
        _state.value = _state.value.copy(startHour = value.filterDigits(), errors = _state.value.errors - Field.TIME)
    }

    fun onStartMinuteChange(value: String) {
        _state.value = _state.value.copy(startMinute = value.filterDigits(), errors = _state.value.errors - Field.TIME)
    }

    fun onEndHourChange(value: String) {
        _state.value = _state.value.copy(endHour = value.filterDigits(), errors = _state.value.errors - Field.TIME)
    }

    fun onEndMinuteChange(value: String) {
        _state.value = _state.value.copy(endMinute = value.filterDigits(), errors = _state.value.errors - Field.TIME)
    }

    fun onSortOrderChange(value: String) {
        _state.value = _state.value.copy(sortOrder = value.filterDigits())
    }

    fun save() {
        val current = _state.value
        val errors = validate(current)
        if (errors.isNotEmpty()) {
            _state.value = current.copy(errors = errors)
            return
        }

        val start = ScheduleValidator.toMinutesOfDay(current.startHour, current.startMinute) ?: return
        val end = ScheduleValidator.toMinutesOfDay(current.endHour, current.endMinute) ?: return

        viewModelScope.launch {
            val entity = ClassTimeEntity(
                id = existing?.id ?: 0,
                shiftId = shiftId,
                name = current.name.trim(),
                startTime = start,
                endTime = end,
                sortOrder = current.sortOrder.toIntOrNull() ?: 0
            )
            if (existing == null) {
                shiftRepository.insertClassTime(entity)
            } else {
                shiftRepository.updateClassTime(entity)
            }
            _state.value = _state.value.copy(saved = true)
        }
    }

    private fun validate(state: UiState): Map<Field, String> {
        val errors = mutableMapOf<Field, String>()
        if (state.name.isBlank()) errors[Field.NAME] = "نام زنگ الزامی است."

        val start = ScheduleValidator.toMinutesOfDay(state.startHour, state.startMinute)
        val end = ScheduleValidator.toMinutesOfDay(state.endHour, state.endMinute)
        if (start == null || end == null) {
            errors[Field.TIME] = "ساعت را به صورت عددی وارد کنید."
        } else {
            val result = ScheduleValidator.validateTimeRange(start, end)
            if (result is ScheduleValidator.TimeRangeResult.Invalid) {
                errors[Field.TIME] = result.reason
            }
        }
        return errors
    }

    private fun String.filterDigits(): String =
        filter { it.isDigit() || it in '۰'..'۹' || it in '٠'..'٩' }.take(2)
}
