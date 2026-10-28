package com.amadino.app.data.repository

import com.amadino.app.data.local.dao.SmsCounterDao
import com.amadino.app.data.local.dao.SmsLogDao
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.SmsLogEntity
import kotlinx.coroutines.flow.Flow

class SmsRepository private constructor(
    private val smsLogDao: SmsLogDao,
    private val smsCounterDao: SmsCounterDao
) {

    fun getSmsLogs(): Flow<List<SmsLogEntity>> = smsLogDao.getAll()
    fun getSmsLogsByStudent(studentId: Int): Flow<List<SmsLogEntity>> =
        smsLogDao.getByStudent(studentId)
    fun getSmsLogsByEventType(eventType: String): Flow<List<SmsLogEntity>> =
        smsLogDao.getByEventType(eventType)
    fun getSmsLogsByDateRange(fromMillis: Long, toMillis: Long): Flow<List<SmsLogEntity>> =
        smsLogDao.getByDateRange(fromMillis, toMillis)

    fun getSmsCounter(): Flow<SmsCounterEntity?> = smsCounterDao.getCounter()

    suspend fun insertSmsLog(log: SmsLogEntity): Long = smsLogDao.insert(log)
    suspend fun insertSmsLogs(vararg logs: SmsLogEntity): List<Long> =
        smsLogDao.insertAll(*logs)

    suspend fun incrementCounter(sent: Long, failed: Long, skipped: Long, lastUpdated: Long) =
        smsCounterDao.increment(sent, failed, skipped, lastUpdated)

    companion object {
        @Volatile
        private var INSTANCE: SmsRepository? = null

        fun getInstance(smsLogDao: SmsLogDao, smsCounterDao: SmsCounterDao): SmsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = SmsRepository(smsLogDao, smsCounterDao)
                INSTANCE!!
            }
        }
    }
}
