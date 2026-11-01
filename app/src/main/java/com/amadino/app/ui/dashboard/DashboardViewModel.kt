package com.amadino.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.WeekDayEntity
import com.amadino.app.data.repository.ShiftRepository
import com.amadino.app.data.repository.SmsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val shiftRepository: ShiftRepository,
    private val smsRepository: SmsRepository
) : ViewModel() {

    private val _selectedDayId = MutableStateFlow(1)
    val selectedDayId: StateFlow<Int> = _selectedDayId.asStateFlow()

    fun selectDay(dayId: Int) {
        _selectedDayId.value = dayId
    }

    val schoolDays: StateFlow<List<WeekDayEntity>> = shiftRepository.getSchoolDays()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val shiftsByDay: StateFlow<List<ShiftEntity>> = _selectedDayId
        .flatMapLatest { dayId -> shiftRepository.getActiveShiftsByDay(dayId) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val smsCounter: StateFlow<SmsCounterEntity?> = smsRepository.getSmsCounter()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            null
        )
}
