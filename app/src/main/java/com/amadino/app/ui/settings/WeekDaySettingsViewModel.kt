package com.amadino.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.WeekDayEntity
import com.amadino.app.data.repository.ShiftRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WeekDaySettingsViewModel(
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    val days: StateFlow<List<WeekDayEntity>> = shiftRepository.getWeekDays()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleSchoolDay(day: WeekDayEntity) {
        viewModelScope.launch {
            shiftRepository.updateWeekDay(day.copy(isSchoolDay = !day.isSchoolDay))
        }
    }
}
