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

data class StudentUiModel(
    val id: String, val name: String, val phone: String,
    val className: String, val school: String, val field: String,
    val currentStatus: String? = null, // PRESENT, ABSENT, LATE
    val currentCheckOut: String? = null // Time of check-out
)
data class ShiftUiModel(val id: String, val time: String)
data class HistoryRecordUiModel(val studentName: String, val status: String, val time: String)
data class HistoryDateUiModel(val date: String, val records: List<HistoryRecordUiModel>)

data class MainUiState(
    val stats: DashboardStats = DashboardStats(0, 0, 0),
    val students: List<StudentUiModel> = emptyList(),
    val shifts: List<ShiftUiModel> = emptyList(),
    val history: List<HistoryDateUiModel> = emptyList(),
    val selectedStudentId: String? = null,
    val activeShiftId: String? = null,
    val smsTemplateIn: String = "",
    val smsTemplateOut: String = "",
    val themeName: String = "CyberViolet",
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
            val currentTheme = withContext(Dispatchers.IO) { userDataManager.getThemeName() }

            val shiftList = mutableListOf<ShiftUiModel>()
            if (shiftsArray != null) {
                for (i in 0 until shiftsArray.length()) {
                    val shift = shiftsArray.getJSONObject(i)
                    shiftList.add(ShiftUiModel(shift.optString("id"), shift.optString("time")))
                }
            }

            val currentActiveShift = _uiState.value.activeShiftId ?: shiftList.firstOrNull()?.id
            val activeShiftTime = shiftList.find { it.id == currentActiveShift }?.time

            var present = 0; var absent = 0; var sms = 0
            val studentList = mutableListOf<StudentUiModel>()
            val historyMap = mutableMapOf<String, MutableList<HistoryRecordUiModel>>()

            if (studentsArray != null) {
                for (i in 0 until studentsArray.length()) {
                    val std = studentsArray.getJSONObject(i)
                    val studentName = std.optString("name")
                    val attendance = std.optJSONObject("attendance")

                    var statusForActiveShift: String? = null
                    var checkOutForActiveShift: String? = null

                    if (activeShiftTime != null) {
                        val slot = attendance?.optJSONObject(today)?.optJSONObject(activeShiftTime)
                        statusForActiveShift = slot?.optString("status")
                        val outTime = slot?.optString("check_out_time")
                        if (outTime != null && outTime != "null" && outTime.isNotEmpty()) {
                            checkOutForActiveShift = outTime
                        }
                    }

                    studentList.add(StudentUiModel(
                        id = std.optString("id"), name = studentName, phone = std.optString("parent_phone"),
                        className = std.optString("class_name", "نامشخص"), school = std.optString("school", "نامشخص"), field = std.optString("field", "نامشخص"),
                        currentStatus = statusForActiveShift,
                        currentCheckOut = checkOutForActiveShift
                    ))

                    if (attendance != null) {
                        val dates = attendance.keys()
                        while (dates.hasNext()) {
                            val date = dates.next()
                            val dayRecord = attendance.optJSONObject(date)

                            if (date == today && dayRecord != null) {
                                val slots = dayRecord.keys()
                                while (slots.hasNext()) {
                                    val slot = dayRecord.getJSONObject(slots.next())
                                    when (slot.optString("status")) {
                                        "PRESENT", "LATE" -> present++
                                        "ABSENT" -> absent++
                                    }
                                    if (slot.optString("sms_status") == "SENT") sms++
                                }
                            }

                            if (dayRecord != null) {
                                val slots = dayRecord.keys()
                                while (slots.hasNext()) {
                                    val slotTime = slots.next()
                                    val slotData = dayRecord.getJSONObject(slotTime)
                                    val status = slotData.optString("status")

                                    if (status != "NOT_RECORDED" && status.isNotEmpty()) {
                                        val statusFa = when(status) {
                                            "PRESENT" -> "حاضر"
                                            "ABSENT" -> "غایب"
                                            "LATE" -> "تاخیر"
                                            else -> status
                                        }
                                        val checkIn = slotData.optString("check_in_time", "--")
                                        val rec = HistoryRecordUiModel(studentName, statusFa, checkIn)
                                        historyMap.getOrPut(date) { mutableListOf() }.add(rec)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val historyList = historyMap.map { (date, records) ->
                HistoryDateUiModel(date, records)
            }.sortedByDescending { it.date }

            _uiState.update {
                it.copy(
                    stats = DashboardStats(present, absent, sms),
                    students = studentList,
                    shifts = shiftList,
                    history = historyList,
                    activeShiftId = currentActiveShift,
                    smsTemplateIn = templates.first,
                    smsTemplateOut = templates.second,
                    themeName = currentTheme,
                    isLoading = false
                )
            }
        }
    }

    fun selectShift(shiftId: String) {
        _uiState.update { it.copy(activeShiftId = shiftId) }
        loadData()
    }

    fun selectStudent(id: String?) {
        _uiState.update { it.copy(selectedStudentId = id) }
    }

    fun performAction(action: String) {
        val studentId = _uiState.value.selectedStudentId ?: return
        val activeShiftId = _uiState.value.activeShiftId ?: return
        val activeShiftTime = _uiState.value.shifts.find { it.id == activeShiftId }?.time ?: return

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userDataManager.updateAttendance(studentId, today, activeShiftTime, action, currentTime)
            }
            loadData()
        }
    }

    fun sendSmsToSelected(smsType: String, onResult: (String) -> Unit) {
        val student = _uiState.value.students.find { it.id == _uiState.value.selectedStudentId } ?: return
        val activeShiftId = _uiState.value.activeShiftId ?: return
        val activeShiftTime = _uiState.value.shifts.find { it.id == activeShiftId }?.time ?: return

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val jalaliDate = getCurrentJalaliDate()
        val template = if (smsType == "IN") _uiState.value.smsTemplateIn else _uiState.value.smsTemplateOut
        val finalMessage = template.replace("{name}", student.name).replace("{date}", jalaliDate).replace("{time}", currentTime)

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { smsSender.send(student.phone, finalMessage) }
            if (result is SmsSendResult.Success) {
                withContext(Dispatchers.IO) { userDataManager.updateSmsStatus(student.id, today, activeShiftTime, "SENT") }
                loadData()
                onResult("پیامک با موفقیت ارسال شد")
            } else {
                onResult("خطا در ارسال پیامک")
            }
        }
    }

    fun changeTheme(themeName: String) {
        viewModelScope.launch { withContext(Dispatchers.IO) { userDataManager.saveThemeName(themeName) }; loadData() }
    }

    fun addShift(time: String) { viewModelScope.launch { withContext(Dispatchers.IO) { userDataManager.addShift(time) }; loadData() } }
    fun updateShift(id: String, newTime: String) { viewModelScope.launch { withContext(Dispatchers.IO) { userDataManager.updateShift(id, newTime) }; loadData() } }
    fun deleteShift(id: String) { viewModelScope.launch { withContext(Dispatchers.IO) { userDataManager.deleteShift(id) }; loadData() } }
    fun addStudent(name: String, phone: String, className: String, school: String, field: String) {
        viewModelScope.launch { withContext(Dispatchers.IO) { userDataManager.addStudent(name, phone, className, school, field) }; loadData() }
    }
    fun saveSmsTemplates(templateIn: String, templateOut: String) {
        viewModelScope.launch { withContext(Dispatchers.IO) { userDataManager.saveSmsTemplates(templateIn, templateOut) }; loadData() }
    }
    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch { withContext(Dispatchers.IO) { userDataManager.clearAllData() }; loadData(); onComplete() }
    }
    fun finalizeDay(onComplete: (Boolean) -> Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) { try { userDataManager.finalizeDay(today); true } catch (e: Exception) { false } }
            if (success) loadData()
            onComplete(success)
        }
    }
}