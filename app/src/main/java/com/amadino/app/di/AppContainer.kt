package com.amadino.app.di

import android.content.Context
import com.amadino.app.data.local.dao.AttendanceDao
import com.amadino.app.data.local.dao.ClassTimeDao
import com.amadino.app.data.local.dao.PaymentDao
import com.amadino.app.data.local.dao.ShiftDao
import com.amadino.app.data.local.dao.SmsCounterDao
import com.amadino.app.data.local.dao.SmsLogDao
import com.amadino.app.data.local.dao.SmsTemplateDao
import com.amadino.app.data.local.dao.StudentDao
import com.amadino.app.data.local.dao.WeekDayDao
import com.amadino.app.data.local.db.AppDatabase
import com.amadino.app.data.repository.AttendanceRepository
import com.amadino.app.data.repository.FinanceRepository
import com.amadino.app.data.repository.ShiftRepository
import com.amadino.app.data.repository.SmsRepository
import com.amadino.app.data.repository.SmsTemplateRepository
import com.amadino.app.data.repository.StudentRepository
import com.amadino.app.data.sms.SmsSender
import com.amadino.app.domain.usecase.SendSmsUseCase

class AppContainer private constructor(
    private val database: AppDatabase,
    context: Context
) {
    val weekDayDao: WeekDayDao = database.weekDayDao()
    val shiftDao: ShiftDao = database.shiftDao()
    val classTimeDao: ClassTimeDao = database.classTimeDao()
    val studentDao: StudentDao = database.studentDao()
    val attendanceDao: AttendanceDao = database.attendanceDao()
    val smsLogDao: SmsLogDao = database.smsLogDao()
    val smsCounterDao: SmsCounterDao = database.smsCounterDao()
    val smsTemplateDao: SmsTemplateDao = database.smsTemplateDao()
    val paymentDao: PaymentDao = database.paymentDao()

    val studentRepository = StudentRepository.getInstance(studentDao, weekDayDao)
    val shiftRepository = ShiftRepository.getInstance(weekDayDao, shiftDao, classTimeDao)
    val attendanceRepository = AttendanceRepository.getInstance(attendanceDao)
    val smsRepository = SmsRepository.getInstance(smsLogDao, smsCounterDao)
    val smsTemplateRepository = SmsTemplateRepository.getInstance(smsTemplateDao)
    val financeRepository = FinanceRepository.getInstance(studentDao, paymentDao)

    val smsSender = SmsSender(context.applicationContext)
    val sendSmsUseCase = SendSmsUseCase(smsRepository, smsTemplateRepository, smsSender)

    companion object {
        @Volatile
        private var INSTANCE: AppContainer? = null

        fun getInstance(context: Context): AppContainer {
            return INSTANCE ?: synchronized(this) {
                val applicationContext = context.applicationContext
                val database = AppDatabase.getDatabase(applicationContext)
                INSTANCE = AppContainer(database, applicationContext)
                INSTANCE!!
            }
        }
    }
}
