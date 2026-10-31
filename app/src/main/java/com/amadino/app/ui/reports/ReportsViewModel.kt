package com.amadino.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.repository.AttendanceRepository
import com.amadino.app.data.repository.FinanceRepository
import com.amadino.app.data.repository.SmsRepository
import com.amadino.app.data.repository.StudentRepository
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ReportsViewModel(
    studentRepository: StudentRepository,
    attendanceRepository: AttendanceRepository,
    financeRepository: FinanceRepository,
    smsRepository: SmsRepository
) : ViewModel() {

    data class Summary(
        val totalStudents: Int = 0,
        val activeStudents: Int = 0,
        val totalDebt: Long = 0,
        val totalPayments: Long = 0,
        val todayPresent: Int = 0,
        val todayLate: Int = 0,
        val todayAbsent: Int = 0,
        val smsSent: Long = 0,
        val smsFailed: Long = 0,
        val smsSkipped: Long = 0
    )

    private val today: Long = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val summary: StateFlow<Summary> = combine(
        studentRepository.getAllStudents(),
        attendanceRepository.getByDate(today),
        financeRepository.getAllPayments(),
        smsRepository.getSmsCounter()
    ) { students, attendance, payments, counter ->
        Summary(
            totalStudents = students.size,
            activeStudents = students.count { it.isActive },
            totalDebt = students.sumOf { it.currentDebt },
            totalPayments = payments.sumOf { it.amount },
            todayPresent = attendance.count { it.status.equals("PRESENT", ignoreCase = true) },
            todayLate = attendance.count { it.status.equals("LATE", ignoreCase = true) },
            todayAbsent = attendance.count { it.status.equals("ABSENT", ignoreCase = true) },
            smsSent = counter?.totalSent ?: 0L,
            smsFailed = counter?.totalFailed ?: 0L,
            smsSkipped = counter?.totalSkipped ?: 0L
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Summary())
}
