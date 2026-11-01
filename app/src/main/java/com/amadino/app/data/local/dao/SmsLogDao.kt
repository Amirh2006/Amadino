package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.SmsLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsLogDao {

    @Query("SELECT * FROM sms_log ORDER BY sent_at DESC")
    fun getAll(): Flow<List<SmsLogEntity>>

    @Query(
        """SELECT * FROM sms_log
           WHERE student_id = :studentId
           ORDER BY sent_at DESC"""
    )
    fun getByStudent(studentId: Int): Flow<List<SmsLogEntity>>

    @Query(
        """SELECT * FROM sms_log
           WHERE event_type = :eventType
           ORDER BY sent_at DESC"""
    )
    fun getByEventType(eventType: String): Flow<List<SmsLogEntity>>

    @Query(
        """SELECT * FROM sms_log
           WHERE sent_at >= :fromMillis
             AND sent_at <= :toMillis
           ORDER BY sent_at DESC"""
    )
    fun getByDateRange(fromMillis: Long, toMillis: Long): Flow<List<SmsLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: SmsLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg logs: SmsLogEntity): List<Long>

    @Update
    suspend fun update(log: SmsLogEntity)

    @Query(
        """UPDATE sms_log
           SET status = :status,
               error_reason = :errorReason,
               destination = :destination,
               fallback_used = :fallbackUsed
           WHERE id = :id"""
    )
    suspend fun updateResult(
        id: Int,
        status: String,
        errorReason: String?,
        destination: String,
        fallbackUsed: Boolean
    )

    @Query("UPDATE sms_log SET delivery_status = :status WHERE id = :id")
    suspend fun updateDeliveryStatus(id: Int, status: String)

    @Delete
    suspend fun delete(log: SmsLogEntity)
}
