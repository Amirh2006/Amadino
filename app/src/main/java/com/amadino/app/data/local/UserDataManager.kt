package com.amadino.app.data.local

import android.content.Context
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

class UserDataManager(private val context: Context) {

    private val fileName = "users.json"
    private val mutex = Mutex()

    suspend fun readUsers(): JSONObject = mutex.withLock {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            val defaultJson = JSONObject().apply {
                put("schema_version", "1.2.1")
                put("shifts", JSONArray().apply { put(JSONObject().put("time", "08:00 - 09:00")) })
                put("students", JSONArray())
            }
            file.writeText(defaultJson.toString())
            return@withLock defaultJson
        }
        return@withLock try { JSONObject(file.readText()) } catch (e: Exception) {
            JSONObject().apply { put("schema_version", "1.2.1"); put("students", JSONArray()) }
        }
    }

    suspend fun writeUsers(json: JSONObject) = mutex.withLock {
        val file = File(context.filesDir, fileName)
        val tempFile = File(context.filesDir, "$fileName.tmp")
        try {
            tempFile.writeText(json.toString(4))
            tempFile.renameTo(file)
        } catch (e: Exception) { Log.e("UserDataManager", "Write error", e) }
    }

    suspend fun updateStudentStatus(studentId: String, date: String, slot: String, status: String) {
        val users = readUsers()
        val students = users.optJSONArray("students") ?: return
        for (i in 0 until students.length()) {
            val student = students.getJSONObject(i)
            if (student.optString("id") == studentId) {
                val attendance = student.optJSONObject("attendance") ?: JSONObject().also { student.put("attendance", it) }
                val dayRecord = attendance.optJSONObject(date) ?: JSONObject().also { attendance.put(date, it) }
                val slotRecord = dayRecord.optJSONObject(slot) ?: JSONObject().also { dayRecord.put(slot, it) }

                slotRecord.put("status", status)
                if (status == "PRESENT") slotRecord.put("check_in_time", "08:05")
                if (status == "ABSENT") {
                    slotRecord.put("check_in_time", JSONObject.NULL)
                    slotRecord.put("check_out_time", JSONObject.NULL)
                }
            }
        }
        writeUsers(users)
    }

    suspend fun updateSmsStatus(studentId: String, date: String, slot: String, smsStatus: String) {
        val users = readUsers()
        val students = users.optJSONArray("students") ?: return
        for (i in 0 until students.length()) {
            val student = students.getJSONObject(i)
            if (student.optString("id") == studentId) {
                val slotRecord = student.optJSONObject("attendance")?.optJSONObject(date)?.optJSONObject(slot)
                slotRecord?.put("sms_status", smsStatus)
            }
        }
        writeUsers(users)
    }

    suspend fun addStudent(name: String, phone: String, className: String, school: String, field: String) {
        val users = readUsers()
        val studentsArray = users.optJSONArray("students") ?: JSONArray().also { users.put("students", it) }
        studentsArray.put(JSONObject().apply {
            put("id", UUID.randomUUID().toString())
            put("name", name)
            put("parent_phone", phone)
            put("class_name", className)
            put("school", school)
            put("field", field)
            put("attendance", JSONObject())
        })
        writeUsers(users)
    }

    suspend fun finalizeDay(date: String) {
        val users = readUsers()
        val studentsArray = users.optJSONArray("students") ?: return
        for (i in 0 until studentsArray.length()) {
            val attendance = studentsArray.getJSONObject(i).optJSONObject("attendance")?.optJSONObject(date) ?: continue
            val keys = attendance.keys()
            while (keys.hasNext()) {
                val slot = attendance.getJSONObject(keys.next())
                if (slot.optString("status") == "NOT_RECORDED") slot.put("status", "ABSENT")
            }
        }
        writeUsers(users)
    }
}