package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amadino.app.data.local.entity.SmsTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsTemplateDao {

    @Query("SELECT * FROM sms_template")
    fun getAll(): Flow<List<SmsTemplateEntity>>

    @Query("SELECT body FROM sms_template WHERE event_type = :eventType LIMIT 1")
    suspend fun getBody(eventType: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(template: SmsTemplateEntity)

    @Query("DELETE FROM sms_template WHERE event_type = :eventType")
    suspend fun deleteByEventType(eventType: String)
}
