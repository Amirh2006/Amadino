package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payment WHERE student_id = :studentId ORDER BY payment_date DESC")
    fun getByStudent(studentId: Int): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payment WHERE id = :id")
    fun getById(id: Int): Flow<PaymentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: PaymentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg payments: PaymentEntity): List<Long>

    @Update
    suspend fun update(payment: PaymentEntity)

    @Delete
    suspend fun delete(payment: PaymentEntity)

    @Query("DELETE FROM payment WHERE id = :id")
    suspend fun deleteById(id: Int)
}
