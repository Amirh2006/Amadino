package com.amadino.app.data.repository

import com.amadino.app.data.local.dao.PaymentDao
import com.amadino.app.data.local.dao.StudentDao
import com.amadino.app.data.local.entity.PaymentEntity
import com.amadino.app.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

class FinanceRepository private constructor(
    private val studentDao: StudentDao,
    private val paymentDao: PaymentDao
) {

    fun getDebtors(): Flow<List<StudentEntity>> = studentDao.getDebtors()
    fun getPaymentsByStudent(studentId: Int): Flow<List<PaymentEntity>> =
        paymentDao.getByStudent(studentId)
    fun getAllPayments(): Flow<List<PaymentEntity>> = paymentDao.getAll()

    suspend fun insertPayment(payment: PaymentEntity): Long = paymentDao.insert(payment)

    companion object {
        @Volatile
        private var INSTANCE: FinanceRepository? = null

        fun getInstance(studentDao: StudentDao, paymentDao: PaymentDao): FinanceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = FinanceRepository(studentDao, paymentDao)
                INSTANCE!!
            }
        }
    }
}
