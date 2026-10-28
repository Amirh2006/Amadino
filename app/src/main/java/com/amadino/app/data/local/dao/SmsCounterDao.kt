package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.SmsCounterEntity

@Dao
interface SmsCounterDao {

    @Query("SELECT * FROM sms_counter WHERE id = 1")
    fun getCounter(): Flow<SmsCounterEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(counter: SmsCounterEntity): Long

    @Update
    suspend fun update(counter: SmsCounterEntity)

    @Query(
        """UPDATE sms_counter
           SET total_sent = total_sent + :sent,
               total_failed = total_failed + :failed,
               total_skipped = total_skipped + :skipped,
               last_updated = :lastUpdated
           WHERE id = 1"""
    )
    suspend fun increment(sent: Long, failed: Long, skipped: Long, lastUpdated: Long)
}
