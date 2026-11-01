package com.amadino.app.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.entity.AttendanceEntity
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.repository.AttendanceRepository
import com.amadino.app.data.repository.ShiftRepository
import com.amadino.app.data.repository.StudentRepository
import com.amadino.app.domain.model.SmsEventType
import com.amadino.app.domain.usecase.SendSmsUseCase
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository,
    shiftRepository: ShiftRepository,
    private val sendSmsUseCase: SendSmsUseCase,
    private val shiftId: Int,
    private val dayId: Int,
    private val classTimeId: Int
) : ViewModel() {

    data class Row(
        val student: StudentEntity,
        val status: String?
    )

    data class SmsPrompt(
        val eventType: SmsEventType,
        val recipients: Int
    )

    data class UiState(
        val shift: ShiftEntity? = null,
        val classTime: ClassTimeEntity? = null,
        val rows: List<Row> = emptyList(),
        val savedCount: Int = 0,
        val pendingCount: Int = 0,
        val message: String? = null,
        val todayRecords: List<AttendanceEntity> = emptyList()
    )

    enum class Status(val value: String) {
        PRESENT("PRESENT"),
        LATE("LATE"),
        ABSENT("ABSENT")
    }

    private val todayDate: LocalDate = LocalDate.now()
    private val today: Long = todayDate
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    private val edits = MutableStateFlow<Map<Int, String>>(emptyMap())
    private val message = MutableStateFlow<String?>(null)
    private val _smsPrompt = MutableStateFlow<SmsPrompt?>(null)
    val smsPrompt: StateFlow<SmsPrompt?> = _smsPrompt.asStateFlow()

    private var lastSavedRecords: List<AttendanceEntity> = emptyList()

    val state: StateFlow<UiState> = combine(
        shiftRepository.getShiftById(shiftId),
        shiftRepository.getClassTimesByShift(shiftId),
        studentRepository.getActiveStudents(),
        attendanceRepository.getByClassTimeAndDate(classTimeId, today),
        edits
    ) { shift, classTimes, students, existing, edited ->
        val existingByStudent = existing.associate { it.studentId to it.status }
        val rows = students.map { student ->
            Row(student = student, status = edited[student.id] ?: existingByStudent[student.id])
        }
        UiState(
            shift = shift,
            classTime = classTimes.firstOrNull { it.id == classTimeId },
            rows = rows,
            savedCount = existing.size,
            pendingCount = edited.size,
            todayRecords = existing
        )
    }.combine(message) { uiState, currentMessage ->
        uiState.copy(message = currentMessage)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UiState()
    )

    fun setStatus(studentId: Int, status: Status) {
        edits.value = edits.value + (studentId to status.value)
    }

    fun markAll(status: Status) {
        val studentIds = state.value.rows.map { it.student.id }
        edits.value = studentIds.associateWith { status.value }
    }

    fun save() {
        val rows = state.value.rows
        val pending = edits.value
        if (pending.isEmpty()) {
            message.value = "تغییری برای ذخیره وجود ندارد."
            return
        }

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val records = rows
                .filter { pending.containsKey(it.student.id) }
                .map { row ->
                    AttendanceEntity(
                        studentId = row.student.id,
                        classTimeId = classTimeId,
                        shiftId = shiftId,
                        dayId = dayId,
                        attendanceDate = today,
                        status = pending.getValue(row.student.id),
                        recordedAt = now
                    )
                }
            records.forEach { attendanceRepository.insertAttendance(it) }
            lastSavedRecords = records
            edits.value = emptyMap()
            message.value = "حضور ${records.size.toPersianCount()} دانش‌آموز ذخیره شد."
            _smsPrompt.value = SmsPrompt(SmsEventType.ENTRY, records.size)
        }
    }

    /** Requests a manual EXIT notification for the records already saved today. */
    fun requestExitSms() {
        val records = state.value.todayRecords
        if (records.isEmpty()) {
            message.value = "ابتدا حضور را ثبت کنید."
            return
        }
        _smsPrompt.value = SmsPrompt(SmsEventType.EXIT, records.size)
    }

    fun dismissSmsPrompt() {
        _smsPrompt.value = null
    }

    fun sendSms(eventType: SmsEventType) {
        val records = when (eventType) {
            SmsEventType.ENTRY -> lastSavedRecords
            SmsEventType.EXIT -> state.value.todayRecords
        }
        _smsPrompt.value = null
        if (records.isEmpty()) {
            message.value = "گیرنده‌ای برای ارسال پیامک وجود ندارد."
            return
        }

        viewModelScope.launch {
            val date = todayDate.format(DATE_FORMATTER)
            val time = LocalTime.now().format(TIME_FORMATTER)
            var success = 0
            var failed = 0
            var skipped = 0

            records.forEach { record ->
                val student = studentRepository.getStudentById(record.studentId).first()
                if (student == null) {
                    skipped++
                    return@forEach
                }
                when (sendSmsUseCase.send(student, eventType, record.id, date, time).status) {
                    "SUCCESS" -> success++
                    "FAILED" -> failed++
                    else -> skipped++
                }
            }
            message.value = "پیامک: ${success.toPersianCount()} موفق، ${failed.toPersianCount()} ناموفق، ${skipped.toPersianCount()} رد شده"
        }
    }

    fun consumeMessage() {
        message.value = null
    }

    private fun Int.toPersianCount(): String =
        toString().map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    }
}
