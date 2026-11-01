package com.amadino.app.ui.sms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.repository.SmsRepository
import com.amadino.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SmsLogViewModel(
    smsRepository: SmsRepository,
    studentRepository: StudentRepository
) : ViewModel() {

    enum class EventFilter { ALL, ENTRY, EXIT }

    data class Row(
        val log: SmsLogEntity,
        val studentName: String
    )

    private val _filter = MutableStateFlow(EventFilter.ALL)
    val filter: StateFlow<EventFilter> = _filter.asStateFlow()

    val counter: StateFlow<SmsCounterEntity?> = smsRepository.getSmsCounter()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val rows: StateFlow<List<Row>> = combine(
        smsRepository.getSmsLogs(),
        studentRepository.getAllStudents(),
        _filter
    ) { logs, students, filter ->
        val names = students.associate { it.id to it.fullName }
        logs.asSequence()
            .filter { log ->
                when (filter) {
                    EventFilter.ALL -> true
                    EventFilter.ENTRY -> log.eventType.equals("ENTRY", ignoreCase = true)
                    EventFilter.EXIT -> log.eventType.equals("EXIT", ignoreCase = true)
                }
            }
            .map { log -> Row(log = log, studentName = names[log.studentId] ?: "دانش‌آموز #${log.studentId}") }
            .toList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setFilter(value: EventFilter) {
        _filter.value = value
    }
}
