package com.amadino.app.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.repository.ShiftRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ClassTimePickerViewModel(
    shiftRepository: ShiftRepository,
    shiftId: Int
) : ViewModel() {

    val shift: StateFlow<ShiftEntity?> = shiftRepository.getShiftById(shiftId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val classTimes: StateFlow<List<ClassTimeEntity>> = shiftRepository.getClassTimesByShift(shiftId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
