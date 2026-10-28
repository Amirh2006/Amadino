package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.ClassTimeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassTimeDao {

    @Query("SELECT * FROM class_time WHERE shift_id = :shiftId ORDER BY sort_order ASC")
    fun getByShift(shiftId: Int): Flow<List<ClassTimeEntity>>

    @Query("SELECT * FROM class_time WHERE id = :id")
    fun getById(id: Int): Flow<ClassTimeEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(classTime: ClassTimeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg classTimes: ClassTimeEntity): List<Long>

    @Update
    suspend fun update(classTime: ClassTimeEntity)

    @Delete
    suspend fun delete(classTime: ClassTimeEntity)

    @Query("DELETE FROM class_time WHERE id = :id")
    suspend fun deleteById(id: Int)
}
