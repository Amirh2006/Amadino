package com.amadino.app.data.repository

import com.amadino.app.data.local.dao.SmsTemplateDao
import com.amadino.app.data.local.entity.SmsTemplateEntity
import com.amadino.app.domain.model.SmsEventType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Stores user overrides for the SMS templates. Absent rows fall back to the
 * built-in defaults in [com.amadino.app.data.sms.SmsTemplates].
 */
class SmsTemplateRepository private constructor(
    private val smsTemplateDao: SmsTemplateDao
) {

    /** Overrides keyed by [SmsEventType.value]; missing keys use the defaults. */
    fun getOverrides(): Flow<Map<String, String>> = smsTemplateDao.getAll()
        .map { rows -> rows.associate { it.eventType to it.body } }

    /** Returns the stored body for [eventType], or null when it was never customized. */
    suspend fun getTemplate(eventType: SmsEventType): String? =
        smsTemplateDao.getBody(eventType.value)

    suspend fun saveTemplate(eventType: SmsEventType, body: String) {
        smsTemplateDao.upsert(
            SmsTemplateEntity(
                eventType = eventType.value,
                body = body,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    /** Removes the override so the built-in default applies again. */
    suspend fun resetTemplate(eventType: SmsEventType) {
        smsTemplateDao.deleteByEventType(eventType.value)
    }

    companion object {
        @Volatile
        private var INSTANCE: SmsTemplateRepository? = null

        fun getInstance(smsTemplateDao: SmsTemplateDao): SmsTemplateRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = SmsTemplateRepository(smsTemplateDao)
                INSTANCE!!
            }
        }
    }
}
