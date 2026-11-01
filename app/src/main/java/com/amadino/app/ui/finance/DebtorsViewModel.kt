package com.amadino.app.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.FinanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DebtorsViewModel(
    financeRepository: FinanceRepository
) : ViewModel() {

    val debtors: StateFlow<List<StudentEntity>> = financeRepository.getDebtors()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
