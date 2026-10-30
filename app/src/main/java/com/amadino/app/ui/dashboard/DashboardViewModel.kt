package com.amadino.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.WeekDayEntity
import com.amadino.app.data.repository.ShiftRepository
import com.amadino.app.data.repository.StudentRepository
import com.amadino.app.data.repository.SmsRepository
import com.amadino.app.data.local.entity.SmsCounterEntity
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharingStarted

class DashboardViewModel(
    private val shiftRepository: ShiftRepository,
    private val studentRepository: StudentRepository,
    private val smsRepository: SmsRepository
) : ViewModel() {

    private val _selectedDayId = androidx.lifecycle.MutableStateFlow<Int>(1)
    val selectedDayId: StateFlow<Int> = _selectedDayId

    fun selectDay(dayId: Int) {
        _selectedDayId.value = dayId
    }

    val schoolDays: StateFlow<List<WeekDayEntity>> = shiftRepository.getSchoolDays()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val shiftsByDay: StateFlow<List<ShiftEntity>> = _selectedDayId
        .flatMapLatest { dayId ->
            shiftRepository.getActiveShiftsByDay(dayId)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val smsCounter: StateFlow<SmsCounterEntity?> = smsRepository.getSmsCounter()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    init {
        viewModelScope.launch {
            // Initialization placeholder
        }
    }
}
