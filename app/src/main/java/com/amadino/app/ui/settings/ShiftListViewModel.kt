package com.amadino.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.WeekDayEntity
import com.amadino.app.data.repository.ShiftRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShiftListViewModel(
    private val shiftRepository: ShiftRepository,
    dayId: Int
) : ViewModel() {

    val day: StateFlow<WeekDayEntity?> = shiftRepository.getWeekDayById(dayId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val shifts: StateFlow<List<ShiftEntity>> = shiftRepository.getShiftsByDay(dayId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun delete(shift: ShiftEntity) {
        viewModelScope.launch { shiftRepository.deleteShift(shift) }
    }
}
