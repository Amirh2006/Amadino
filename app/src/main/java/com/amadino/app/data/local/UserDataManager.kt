
package com.amadino.app.data.local

import android.content.Context
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Manages safe read/write operations for the users.json local database.
 * Uses Mutex to prevent concurrent writes and atomic file replacement.
 */
class UserDataManager(private val context: Context) {

    private val fileName = "users.json"
    private val mutex = Mutex()

    /**
     * Safely reads the users.json file.
     * If the file doesn't exist, it creates a default structure.
     */
    suspend fun readUsers(): JSONObject = mutex.withLock {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            val defaultJson = JSONObject().apply {
                put("schema_version", "1.2.0")
                put("students", JSONArray())
            }
            file.writeText(defaultJson.toString())
            return@withLock defaultJson
        }

        return@withLock try {
            JSONObject(file.readText())
        } catch (e: Exception) {
            Log.e("UserDataManager", "Error reading JSON, returning empty structure", e)
            JSONObject().apply {
                put("schema_version", "1.2.0")
                put("students", JSONArray())
            }
        }
    }

    /**
     * Safely writes to users.json using an atomic write pattern.
     * (Writes to a .tmp file, then replaces the original).
     */
    suspend fun writeUsers(json: JSONObject) = mutex.withLock {
        val file = File(context.filesDir, fileName)
        val tempFile = File(context.filesDir, "$fileName.tmp")

        try {
            // 1. Write to temp file
            tempFile.writeText(json.toString(4)) // 4 spaces indent for readability

            // 2. Replace original file (Atomic operation)
            if (tempFile.renameTo(file)) {
                Log.d("UserDataManager", "users.json updated successfully")
            } else {
                Log.e("UserDataManager", "Failed to replace users.json")
            }
        } catch (e: Exception) {
            Log.e("UserDataManager", "Error writing to JSON", e)
        }
    }

    /**
     * Finalize Logic: Marks all unfilled slots for TODAY as ABSENT.
     * This should be called when the user clicks "تایید و ثبت نهایی".
     */
    suspend fun finalizeDay(date: String) {
        val users = readUsers()
        val studentsArray = users.optJSONArray("students") ?: return

        for (i in 0 until studentsArray.length()) {
            val student = studentsArray.getJSONObject(i)
            // Ensure attendance object exists
            val attendance = student.optJSONObject("attendance") ?: JSONObject().also {
                student.put("attendance", it)
            }

            // Get or create the day's record
            val dayRecord = attendance.optJSONObject(date) ?: JSONObject().also {
                attendance.put(date, it)
            }

            // Here we assume we have a list of all possible slots.
            // For now, if a slot exists but is NOT_RECORDED, we mark it ABSENT.
            // If your app defines slots globally, you can iterate over them here.
            val keys = dayRecord.keys()
            while (keys.hasNext()) {
                val slotTime = keys.next()
                val slotData = dayRecord.getJSONObject(slotTime)

                if (slotData.optString("status") == "NOT_RECORDED") {
                    slotData.put("status", "ABSENT")
                    Log.d("UserDataManager", "Marked ${student.optString("name")} as ABSENT for $slotTime")
                }
            }
        }

        // Save back to file
        writeUsers(users)
    }
}