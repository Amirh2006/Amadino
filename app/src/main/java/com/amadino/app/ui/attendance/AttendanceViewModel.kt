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
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    studentRepository: StudentRepository,
    shiftRepository: ShiftRepository,
    private val shiftId: Int,
    private val dayId: Int,
    private val classTimeId: Int
) : ViewModel() {

    data class Row(
        val student: StudentEntity,
        val status: String?
    )

    data class UiState(
        val shift: ShiftEntity? = null,
        val classTime: ClassTimeEntity? = null,
        val rows: List<Row> = emptyList(),
        val savedCount: Int = 0,
        val pendingCount: Int = 0,
        val message: String? = null
    )

    enum class Status(val value: String) {
        PRESENT("PRESENT"),
        LATE("LATE"),
        ABSENT("ABSENT")
    }

    private val today: Long = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    private val edits = MutableStateFlow<Map<Int, String>>(emptyMap())
    private val message = MutableStateFlow<String?>(null)

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
            pendingCount = edited.size
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
            edits.value = emptyMap()
            message.value = "حضور ${records.size.toPersianCount()} دانش‌آموز ذخیره شد."
        }
    }

    fun consumeMessage() {
        message.value = null
    }

    private fun Int.toPersianCount(): String =
        toString().map { if (it in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[it - '0'] else it }.joinToString("")
}
