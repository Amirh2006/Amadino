package com.amadino.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amadino.app.data.local.UserDataManager
import com.amadino.app.data.sms.SmsSendResult
import com.amadino.app.data.sms.SmsSender
import com.amadino.app.ui.components.DashboardStats
import com.amadino.app.utils.getCurrentJalaliDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class StudentUiModel(val id: String, val name: String, val phone: String)
data class ShiftUiModel(val id: String, val time: String)

data class MainUiState(
    val stats: DashboardStats = DashboardStats(0, 0, 0),
    val students: List<StudentUiModel> = emptyList(),
    val shifts: List<ShiftUiModel> = emptyList(), // Added shifts list
    val selectedStudentId: String? = null,
    val smsTemplateIn: String = "",
    val smsTemplateOut: String = "",
    val isLoading: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val userDataManager = UserDataManager(application.applicationContext)
    private val smsSender = SmsSender(application.applicationContext)

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val json = withContext(Dispatchers.IO) { userDataManager.readUsers() }
            val studentsArray = json.optJSONArray("students")
            val shiftsArray = json.optJSONArray("shifts")
            val templates = withContext(Dispatchers.IO) { userDataManager.getSmsTemplates() }

            var present = 0; var absent = 0; var sms = 0
            val studentList = mutableListOf<StudentUiModel>()
            val shiftList = mutableListOf<ShiftUiModel>()

            if (shiftsArray != null) {
                for (i in 0 until shiftsArray.length()) {
                    val shift = shiftsArray.getJSONObject(i)
                    shiftList.add(ShiftUiModel(shift.optString("id"), shift.optString("time")))
                }
            }

            if (studentsArray != null) {
                for (i in 0 until studentsArray.length()) {
                    val std = studentsArray.getJSONObject(i)
                    studentList.add(StudentUiModel(std.optString("id"), std.optString("name"), std.optString("parent_phone")))

                    val att = std.optJSONObject("attendance")?.optJSONObject(today)
                    if (att != null) {
                        val keys = att.keys()
                        while (keys.hasNext()) {
                            val slot = att.getJSONObject(keys.next())
                            when (slot.optString("status")) {
                                "PRESENT", "LATE" -> present++
                                "ABSENT" -> absent++
                            }
                            if (slot.optString("sms_status") == "SENT") sms++
                        }
                    }
                }
            }
            _uiState.update {
                it.copy(
                    stats = DashboardStats(present, absent, sms),
                    students = studentList,
                    shifts = shiftList,
                    smsTemplateIn = templates.first,
                    smsTemplateOut = templates.second,
                    isLoading = false
                )
            }
        }
    }

    // --- Shift Methods ---
    fun addShift(time: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userDataManager.addShift(time) }
            loadData()
        }
    }

    fun updateShift(id: String, newTime: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userDataManager.updateShift(id, newTime) }
            loadData()
        }
    }

    fun deleteShift(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userDataManager.deleteShift(id) }
            loadData()
        }
    }
    // --------------------

    fun selectStudent(id: String?) {
        _uiState.update { it.copy(selectedStudentId = id) }
    }

    fun performAction(action: String) {
        val studentId = _uiState.value.selectedStudentId ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentSlot = _uiState.value.shifts.firstOrNull()?.time ?: "08:00 - 09:00"

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (action) {
                    "CHECK_IN" -> userDataManager.updateStudentStatus(studentId, today, currentSlot, "PRESENT")
                    "CHECK_OUT" -> userDataManager.updateStudentStatus(studentId, today, currentSlot, "ABSENT")
                }
            }
            loadData()
        }
    }

    fun sendSmsToSelected(smsType: String, onResult: (String) -> Unit) {
        val student = _uiState.value.students.find { it.id == _uiState.value.selectedStudentId } ?: return
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentSlot = _uiState.value.shifts.firstOrNull()?.time ?: "08:00 - 09:00"

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val jalaliDate = getCurrentJalaliDate()

        val template = if (smsType == "IN") _uiState.value.smsTemplateIn else _uiState.value.smsTemplateOut

        val finalMessage = template
            .replace("{name}", student.name)
            .replace("{date}", jalaliDate)
            .replace("{time}", currentTime)

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                smsSender.send(student.phone, finalMessage)
            }
            if (result is SmsSendResult.Success) {
                withContext(Dispatchers.IO) { userDataManager.updateSmsStatus(student.id, today, currentSlot, "SENT") }
                loadData()
                onResult("پیامک با موفقیت ارسال شد")
            } else {
                onResult("خطا در ارسال پیامک")
            }
        }
    }

    fun addStudent(name: String, phone: String, className: String, school: String, field: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userDataManager.addStudent(name, phone, className, school, field) }
            loadData()
        }
    }

    fun saveSmsTemplates(templateIn: String, templateOut: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userDataManager.saveSmsTemplates(templateIn, templateOut) }
            loadData()
        }
    }

    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { userDataManager.clearAllData() }
            loadData()
            onComplete()
        }
    }

    fun finalizeDay(onComplete: (Boolean) -> Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                try { userDataManager.finalizeDay(today); true } catch (e: Exception) { false }
            }
            if (success) loadData()
            onComplete(success)
        }
    }
}