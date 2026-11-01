package com.amadino.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.repository.ShiftRepository
import com.amadino.app.domain.validation.ScheduleValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShiftFormViewModel(
    private val shiftRepository: ShiftRepository,
    private val dayId: Int,
    private val shiftId: Int?
) : ViewModel() {

    enum class Field { NAME, MONITOR, TIME }

    data class UiState(
        val isNew: Boolean = false,
        val name: String = "",
        val monitorName: String = "",
        val startHour: String = "08",
        val startMinute: String = "00",
        val endHour: String = "12",
        val endMinute: String = "00",
        val isActive: Boolean = true,
        val errors: Map<Field, String> = emptyMap(),
        val saved: Boolean = false
    )

    private val _state = MutableStateFlow(UiState(isNew = shiftId == null))
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var existing: ShiftEntity? = null

    init {
        if (shiftId != null) {
            viewModelScope.launch {
                shiftRepository.getShiftById(shiftId).first()?.let { shift ->
                    existing = shift
                    val (startHour, startMinute) = ScheduleValidator.fromMinutesOfDay(shift.startTime)
                    val (endHour, endMinute) = ScheduleValidator.fromMinutesOfDay(shift.endTime)
                    _state.value = UiState(
                        isNew = false,
                        name = shift.name,
                        monitorName = shift.monitorName,
                        startHour = startHour,
                        startMinute = startMinute,
                        endHour = endHour,
                        endMinute = endMinute,
                        isActive = shift.isActive
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _state.value = _state.value.copy(name = value, errors = _state.value.errors - Field.NAME)
    }

    fun onMonitorChange(value: String) {
        _state.value = _state.value.copy(monitorName = value, errors = _state.value.errors - Field.MONITOR)
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

        val start = ScheduleValidator.toMinutesOfDay(current.startHour, current.startMinute) ?: return
        val end = ScheduleValidator.toMinutesOfDay(current.endHour, current.endMinute) ?: return

        viewModelScope.launch {
            val entity = ShiftEntity(
                id = existing?.id ?: 0,
                dayId = dayId,
                name = current.name.trim(),
                startTime = start,
                endTime = end,
                monitorName = current.monitorName.trim(),
                isActive = current.isActive
            )
            if (existing == null) {
                shiftRepository.insertShift(entity)
            } else {
                shiftRepository.updateShift(entity)
            }
            _state.value = _state.value.copy(saved = true)
        }
    }

    private fun validate(state: UiState): Map<Field, String> {
        val errors = mutableMapOf<Field, String>()
        if (state.name.isBlank()) errors[Field.NAME] = "نام شیفت الزامی است."

        val start = ScheduleValidator.toMinutesOfDay(state.startHour, state.startMinute)
        val end = ScheduleValidator.toMinutesOfDay(state.endHour, state.endMinute)
        if (start == null || end == null) {
            errors[Field.TIME] = "ساعت را به صورت عددی (مثلاً 08 و 30) وارد کنید."
        } else {
            val result = ScheduleValidator.validateTimeRange(start, end)
            if (result is ScheduleValidator.TimeRangeResult.Invalid) {
                errors[Field.TIME] = result.reason
            }
        }
        return errors
    }

    private fun String.filterDigits(): String = filter { it.isDigit() || it in '۰'..'۹' || it in '٠'..'٩' }.take(2)
}
