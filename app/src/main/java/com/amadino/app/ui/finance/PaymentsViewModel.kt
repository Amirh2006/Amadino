package com.amadino.app.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.PaymentEntity
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.FinanceRepository
import com.amadino.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PaymentsViewModel(
    private val financeRepository: FinanceRepository,
    private val studentRepository: StudentRepository,
    private val studentId: Int
) : ViewModel() {

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    val student: StateFlow<StudentEntity?> = studentRepository.getStudentById(studentId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val payments: StateFlow<List<PaymentEntity>> = financeRepository.getPaymentsByStudent(studentId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onAmountChange(value: String) {
        _amount.value = value.filter { it.isDigit() || it in '۰'..'۹' || it in '٠'..'٩' }
    }

    fun recordPayment() {
        val amount = _amount.value.toPersianNormalized().toLongOrNull() ?: 0L
        if (amount <= 0L) {
            _message.value = "مبلغ پرداخت باید بزرگ‌تر از صفر باشد."
            return
        }

        viewModelScope.launch {
            val current = studentRepository.getStudentById(studentId).first() ?: return@launch
            val now = System.currentTimeMillis()
            financeRepository.insertPayment(
                PaymentEntity(
                    studentId = studentId,
                    amount = amount,
                    paymentDate = now,
                    recordedAt = now
                )
            )
            val newDebt = (current.currentDebt - amount).coerceAtLeast(0L)
            studentRepository.updateStudent(
                current.copy(currentDebt = newDebt, updatedAt = now)
            )
            _amount.value = ""
            _message.value = "پرداخت ثبت شد. بدهی جدید: ${newDebt.toPersianString()}"
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    private fun String.toPersianNormalized(): String = buildString(length) {
        for (char in this@toPersianNormalized) {
            append(
                when (char) {
                    in '۰'..'۹' -> '0' + (char - '۰')
                    in '٠'..'٩' -> '0' + (char - '٠')
                    else -> char
                }
            )
        }
    }

    private fun Long.toPersianString(): String =
        toString().map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")
}
