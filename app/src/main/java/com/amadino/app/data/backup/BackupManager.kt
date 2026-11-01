package com.amadino.app.data.backup

import android.content.ContentResolver
import android.net.Uri
import androidx.room.withTransaction
import com.amadino.app.data.local.db.AppDatabase
import com.amadino.app.data.local.entity.AttendanceEntity
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.local.entity.PaymentEntity
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.local.entity.SmsTemplateEntity
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.local.entity.WeekDayEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/** Counts produced by an export or a restore, used for user feedback. */
data class BackupSummary(
    val weekDays: Int,
    val shifts: Int,
    val classTimes: Int,
    val students: Int,
    val attendance: Int,
    val smsLogs: Int,
    val payments: Int,
    val templates: Int
) {
    val total: Int
        get() = weekDays + shifts + classTimes + students + attendance + smsLogs +
            payments + templates
}

class BackupException(message: String) : Exception(message)

/**
 * Full local backup of every table as a single JSON document, written to a
 * user-chosen [Uri] through the Storage Access Framework. No network, no
 * intermediate files.
 *
 * Restore is a **replace-all** operation inside one transaction: the existing
 * data is cleared and the file's rows are inserted with their original ids so
 * foreign keys stay intact.
 */
class BackupManager(
    private val db: AppDatabase,
    private val contentResolver: ContentResolver
) {

    suspend fun exportTo(uri: Uri): BackupSummary = withContext(Dispatchers.IO) {
        val (json, summary) = buildDocument()
        val stream = contentResolver.openOutputStream(uri, "wt")
            ?: throw BackupException("نوشتن فایل ممکن نشد.")
        stream.use { it.write(json.toByteArray(Charsets.UTF_8)) }
        summary
    }

    suspend fun importFrom(uri: Uri): BackupSummary = withContext(Dispatchers.IO) {
        val text = contentResolver.openInputStream(uri)?.use { input ->
            input.readBytes().toString(Charsets.UTF_8)
        } ?: throw BackupException("خواندن فایل ممکن نشد.")
        restore(text)
    }

    /** Builds the JSON document and its summary without touching the file system. */
    suspend fun buildDocument(): Pair<String, BackupSummary> {
        val dao = db.backupDao()
        val weekDays = dao.weekDays()
        val shifts = dao.shifts()
        val classTimes = dao.classTimes()
        val students = dao.students()
        val attendance = dao.attendance()
        val smsLogs = dao.smsLogs()
        val payments = dao.payments()
        val templates = dao.smsTemplates()

        val root = JSONObject().apply {
            put("app", APP_ID)
            put("version", FORMAT_VERSION)
            put("exportedAt", System.currentTimeMillis())
            put("weekDays", JSONArray(weekDays.map { it.toJson() }))
            put("shifts", JSONArray(shifts.map { it.toJson() }))
            put("classTimes", JSONArray(classTimes.map { it.toJson() }))
            put("students", JSONArray(students.map { it.toJson() }))
            put("attendance", JSONArray(attendance.map { it.toJson() }))
            put("smsLogs", JSONArray(smsLogs.map { it.toJson() }))
            put("payments", JSONArray(payments.map { it.toJson() }))
            put("smsCounter", dao.smsCounters().firstOrNull()?.toJson() ?: JSONObject.NULL)
            put("smsTemplates", JSONArray(templates.map { it.toJson() }))
        }

        val summary = BackupSummary(
            weekDays = weekDays.size,
            shifts = shifts.size,
            classTimes = classTimes.size,
            students = students.size,
            attendance = attendance.size,
            smsLogs = smsLogs.size,
            payments = payments.size,
            templates = templates.size
        )
        return root.toString(2) to summary
    }

    /** Parses and applies a backup document, replacing all current data. */
    suspend fun restore(text: String): BackupSummary {
        val root = try {
            JSONObject(text)
        } catch (error: Exception) {
            throw BackupException("فایل پشتیبان معتبر نیست.")
        }

        if (root.optString("app") != APP_ID) {
            throw BackupException("این فایل متعلق به آمادینو نیست.")
        }
        val version = root.optInt("version", -1)
        if (version != FORMAT_VERSION) {
            throw BackupException("نسخه فایل پشتیبان پشتیبانی نمی‌شود ($version).")
        }

        val weekDays = root.array("weekDays").map { it.toWeekDay() }
        val shifts = root.array("shifts").map { it.toShift() }
        val classTimes = root.array("classTimes").map { it.toClassTime() }
        val students = root.array("students").map { it.toStudent() }
        val attendance = root.array("attendance").map { it.toAttendance() }
        val smsLogs = root.array("smsLogs").map { it.toSmsLog() }
        val payments = root.array("payments").map { it.toPayment() }
        val templates = root.array("smsTemplates").map { it.toSmsTemplate() }
        val counter = root.optJSONObject("smsCounter")?.toSmsCounter()

        db.withTransaction {
            val dao = db.backupDao()
            // Children first, then parents.
            dao.clearSmsLogs()
            dao.clearAttendance()
            dao.clearPayments()
            dao.clearClassTimes()
            dao.clearShifts()
            dao.clearStudents()
            dao.clearWeekDays()
            dao.clearSmsCounter()
            dao.clearSmsTemplates()

            if (weekDays.isNotEmpty()) db.weekDayDao().insertAll(*weekDays.toTypedArray())
            if (shifts.isNotEmpty()) db.shiftDao().insertAll(*shifts.toTypedArray())
            if (classTimes.isNotEmpty()) db.classTimeDao().insertAll(*classTimes.toTypedArray())
            if (students.isNotEmpty()) db.studentDao().insertAll(*students.toTypedArray())
            if (attendance.isNotEmpty()) db.attendanceDao().insertAll(*attendance.toTypedArray())
            if (smsLogs.isNotEmpty()) db.smsLogDao().insertAll(*smsLogs.toTypedArray())
            if (payments.isNotEmpty()) db.paymentDao().insertAll(*payments.toTypedArray())
            if (templates.isNotEmpty()) templates.forEach { db.smsTemplateDao().upsert(it) }
            counter?.let { db.smsCounterDao().insert(it) }
        }

        return BackupSummary(
            weekDays = weekDays.size,
            shifts = shifts.size,
            classTimes = classTimes.size,
            students = students.size,
            attendance = attendance.size,
            smsLogs = smsLogs.size,
            payments = payments.size,
            templates = templates.size
        )
    }

    private companion object {
        const val APP_ID = "amadino"
        const val FORMAT_VERSION = 1
    }
}

// ---------------------------------------------------------------- encoding

private fun JSONObject.putNullable(key: String, value: Any?) {
    put(key, value ?: JSONObject.NULL)
}

private fun WeekDayEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("name", name)
    put("code", code)
    put("isSchoolDay", isSchoolDay)
    put("sortOrder", sortOrder)
}

private fun ShiftEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("dayId", dayId)
    put("name", name)
    put("startTime", startTime)
    put("endTime", endTime)
    put("monitorName", monitorName)
    put("isActive", isActive)
}

private fun ClassTimeEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("shiftId", shiftId)
    put("name", name)
    put("startTime", startTime)
    put("endTime", endTime)
    put("sortOrder", sortOrder)
}

private fun StudentEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("fullName", fullName)
    put("fatherName", fatherName)
    putNullable("nationalId", nationalId)
    put("grade", grade)
    put("mobile1", mobile1)
    putNullable("mobile2", mobile2)
    put("totalTuition", totalTuition)
    put("currentDebt", currentDebt)
    put("isActive", isActive)
    put("createdAt", createdAt)
    put("updatedAt", updatedAt)
}

private fun AttendanceEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("studentId", studentId)
    put("classTimeId", classTimeId)
    put("shiftId", shiftId)
    put("dayId", dayId)
    put("attendanceDate", attendanceDate)
    put("status", status)
    put("recordedAt", recordedAt)
    putNullable("recordedBy", recordedBy)
}

private fun SmsLogEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("studentId", studentId)
    put("attendanceId", attendanceId ?: JSONObject.NULL)
    put("eventType", eventType)
    put("destination", destination)
    put("messageBody", messageBody)
    put("status", status)
    putNullable("errorReason", errorReason)
    put("sentAt", sentAt)
    put("fallbackUsed", fallbackUsed)
    putNullable("deliveryStatus", deliveryStatus)
}

private fun PaymentEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("studentId", studentId)
    put("amount", amount)
    put("paymentDate", paymentDate)
    put("recordedAt", recordedAt)
}

private fun SmsCounterEntity.toJson() = JSONObject().apply {
    put("id", id)
    put("totalSent", totalSent)
    put("totalFailed", totalFailed)
    put("totalSkipped", totalSkipped)
    put("lastUpdated", lastUpdated)
}

private fun SmsTemplateEntity.toJson() = JSONObject().apply {
    put("eventType", eventType)
    put("body", body)
    put("updatedAt", updatedAt)
}

// ---------------------------------------------------------------- decoding

private fun JSONObject.array(key: String): List<JSONObject> {
    val array = optJSONArray(key) ?: return emptyList()
    return (0 until array.length()).mapNotNull { array.optJSONObject(it) }
}

private fun JSONObject.stringOrNull(key: String): String? =
    if (isNull(key) || !has(key)) null else optString(key)

private fun JSONObject.intOrNull(key: String): Int? =
    if (isNull(key) || !has(key)) null else optInt(key)

private fun JSONObject.toWeekDay() = WeekDayEntity(
    id = getInt("id"),
    name = getString("name"),
    code = getString("code"),
    isSchoolDay = optBoolean("isSchoolDay", true),
    sortOrder = optInt("sortOrder", 0)
)

private fun JSONObject.toShift() = ShiftEntity(
    id = getInt("id"),
    dayId = getInt("dayId"),
    name = getString("name"),
    startTime = getLong("startTime"),
    endTime = getLong("endTime"),
    monitorName = optString("monitorName", ""),
    isActive = optBoolean("isActive", true)
)

private fun JSONObject.toClassTime() = ClassTimeEntity(
    id = getInt("id"),
    shiftId = getInt("shiftId"),
    name = getString("name"),
    startTime = getLong("startTime"),
    endTime = getLong("endTime"),
    sortOrder = optInt("sortOrder", 0)
)

private fun JSONObject.toStudent() = StudentEntity(
    id = getInt("id"),
    fullName = getString("fullName"),
    fatherName = getString("fatherName"),
    nationalId = stringOrNull("nationalId"),
    grade = getString("grade"),
    mobile1 = getString("mobile1"),
    mobile2 = stringOrNull("mobile2"),
    totalTuition = optLong("totalTuition", 0),
    currentDebt = optLong("currentDebt", 0),
    isActive = optBoolean("isActive", true),
    createdAt = optLong("createdAt", System.currentTimeMillis()),
    updatedAt = optLong("updatedAt", System.currentTimeMillis())
)

private fun JSONObject.toAttendance() = AttendanceEntity(
    id = getInt("id"),
    studentId = getInt("studentId"),
    classTimeId = getInt("classTimeId"),
    shiftId = getInt("shiftId"),
    dayId = getInt("dayId"),
    attendanceDate = getLong("attendanceDate"),
    status = getString("status"),
    recordedAt = optLong("recordedAt", System.currentTimeMillis()),
    recordedBy = stringOrNull("recordedBy")
)

private fun JSONObject.toSmsLog() = SmsLogEntity(
    id = getInt("id"),
    studentId = getInt("studentId"),
    attendanceId = intOrNull("attendanceId"),
    eventType = getString("eventType"),
    destination = optString("destination", ""),
    messageBody = optString("messageBody", ""),
    status = getString("status"),
    errorReason = stringOrNull("errorReason"),
    sentAt = optLong("sentAt", System.currentTimeMillis()),
    fallbackUsed = optBoolean("fallbackUsed", false),
    deliveryStatus = stringOrNull("deliveryStatus")
)

private fun JSONObject.toPayment() = PaymentEntity(
    id = getInt("id"),
    studentId = getInt("studentId"),
    amount = getLong("amount"),
    paymentDate = optLong("paymentDate", System.currentTimeMillis()),
    recordedAt = optLong("recordedAt", System.currentTimeMillis())
)

private fun JSONObject.toSmsCounter() = SmsCounterEntity(
    id = optInt("id", 1),
    totalSent = optLong("totalSent", 0),
    totalFailed = optLong("totalFailed", 0),
    totalSkipped = optLong("totalSkipped", 0),
    lastUpdated = optLong("lastUpdated", System.currentTimeMillis())
)

private fun JSONObject.toSmsTemplate() = SmsTemplateEntity(
    eventType = getString("eventType"),
    body = getString("body"),
    updatedAt = optLong("updatedAt", System.currentTimeMillis())
)
