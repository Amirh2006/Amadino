package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.amadino.app.data.local.entity.AttendanceEntity
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.local.entity.PaymentEntity
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.local.entity.SmsTemplateEntity
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.local.entity.WeekDayEntity

/**
 * Whole-table reads and clears used only by the backup/restore flow. Keeping
 * them here avoids adding `getAllOnce`/`deleteAll` variants to every DAO.
 */
@Dao
interface BackupDao {

    @Query("SELECT * FROM week_day") suspend fun weekDays(): List<WeekDayEntity>
    @Query("SELECT * FROM shift") suspend fun shifts(): List<ShiftEntity>
    @Query("SELECT * FROM class_time") suspend fun classTimes(): List<ClassTimeEntity>
    @Query("SELECT * FROM student") suspend fun students(): List<StudentEntity>
    @Query("SELECT * FROM attendance") suspend fun attendance(): List<AttendanceEntity>
    @Query("SELECT * FROM sms_log") suspend fun smsLogs(): List<SmsLogEntity>
    @Query("SELECT * FROM sms_counter") suspend fun smsCounters(): List<SmsCounterEntity>
    @Query("SELECT * FROM payment") suspend fun payments(): List<PaymentEntity>
    @Query("SELECT * FROM sms_template") suspend fun smsTemplates(): List<SmsTemplateEntity>

    @Query("DELETE FROM sms_log") suspend fun clearSmsLogs()
    @Query("DELETE FROM attendance") suspend fun clearAttendance()
    @Query("DELETE FROM payment") suspend fun clearPayments()
    @Query("DELETE FROM class_time") suspend fun clearClassTimes()
    @Query("DELETE FROM shift") suspend fun clearShifts()
    @Query("DELETE FROM student") suspend fun clearStudents()
    @Query("DELETE FROM week_day") suspend fun clearWeekDays()
    @Query("DELETE FROM sms_counter") suspend fun clearSmsCounter()
    @Query("DELETE FROM sms_template") suspend fun clearSmsTemplates()
}
