package com.amadino.app.ui.sms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.repository.SmsRepository
import com.amadino.app.data.repository.StudentRepository
import com.amadino.app.domain.model.SmsEventType
import com.amadino.app.domain.usecase.SendSmsUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SmsLogViewModel(
    smsRepository: SmsRepository,
    private val studentRepository: StudentRepository,
    private val sendSmsUseCase: SendSmsUseCase
) : ViewModel() {

    enum class EventFilter { ALL, ENTRY, EXIT }

    data class Row(
        val log: SmsLogEntity,
        val studentName: String
    )

    private val _filter = MutableStateFlow(EventFilter.ALL)
    val filter: StateFlow<EventFilter> = _filter.asStateFlow()

    private val _retryingIds = MutableStateFlow<Set<Int>>(emptySet())
    val retryingIds: StateFlow<Set<Int>> = _retryingIds.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

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

    /** Re-sends a failed or skipped message using the original event and timestamp. */
    fun retry(log: SmsLogEntity) {
        if (log.status == STATUS_SUCCESS || log.id in _retryingIds.value) return

        _retryingIds.update { it + log.id }
        viewModelScope.launch {
            val student = studentRepository.getStudentById(log.studentId).first()
            if (student == null) {
                _message.value = "دانش‌آموز این پیامک یافت نشد."
            } else {
                val eventType = SmsEventType.entries
                    .firstOrNull { it.value.equals(log.eventType, ignoreCase = true) }
                    ?: SmsEventType.ENTRY
                val date = DATE_FORMATTER.format(Date(log.sentAt))
                val time = TIME_FORMATTER.format(Date(log.sentAt))
                val result = sendSmsUseCase.send(
                    student = student,
                    eventType = eventType,
                    attendanceId = log.attendanceId,
                    date = date,
                    time = time
                )
                _message.value = if (result.status == STATUS_SUCCESS) {
                    "پیامک با موفقیت ارسال شد."
                } else {
                    "ارسال پیامک ناموفق بود: ${result.errorReason ?: "خطای نامشخص"}"
                }
            }
            _retryingIds.update { it - log.id }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }

    /** Shows a transient message in the log screen snackbar. */
    fun reportMessage(text: String) {
        _message.value = text
    }

    private companion object {
        const val STATUS_SUCCESS = "SUCCESS"
        val DATE_FORMATTER = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        val TIME_FORMATTER = SimpleDateFormat("HH:mm", Locale.US)
    }
}
