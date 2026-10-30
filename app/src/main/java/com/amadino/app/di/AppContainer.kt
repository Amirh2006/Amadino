package com.amadino.app.di

import android.content.Context
import com.amadino.app.data.local.db.AppDatabase
import com.amadino.app.data.local.dao.*
import com.amadino.app.data.repository.*

class AppContainer private constructor(
    private val database: AppDatabase
) {
    val weekDayDao = database.weekDayDao()
    val shiftDao = database.shiftDao()
    val classTimeDao = database.classTimeDao()
    val studentDao = database.studentDao()
    val attendanceDao = database.attendanceDao()
    val smsLogDao = database.smsLogDao()
    val smsCounterDao = database.smsCounterDao()
    val paymentDao = database.paymentDao()

    val studentRepository = StudentRepository.getInstance(studentDao, weekDayDao)
    val shiftRepository = ShiftRepository.getInstance(weekDayDao, shiftDao, classTimeDao)
    val attendanceRepository = AttendanceRepository.getInstance(attendanceDao)
    val smsRepository = SmsRepository.getInstance(smsLogDao, smsCounterDao)
    val financeRepository = FinanceRepository.getInstance(studentDao, paymentDao)

    companion object {
        @Volatile
        private var INSTANCE: AppContainer? = null

        fun getInstance(context: Context): AppContainer {
            return INSTANCE ?: synchronized(this) {
                val database = AppDatabase.getDatabase(context.applicationContext)
                INSTANCE = AppContainer(database)
                INSTANCE!!
            }
        }
    }
}
