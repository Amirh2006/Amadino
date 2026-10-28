package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM student ORDER BY full_name ASC")
    fun getAll(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM student WHERE is_active = 1 ORDER BY full_name ASC")
    fun getActive(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM student WHERE id = :id")
    fun getById(id: Int): Flow<StudentEntity?>

    @Query("SELECT * FROM student WHERE national_id = :nationalId")
    fun getByNationalId(nationalId: String): Flow<StudentEntity?>

    @Query("SELECT * FROM student WHERE current_debt > 0 ORDER BY full_name ASC")
    fun getDebtors(): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg students: StudentEntity): List<Long>

    @Update
    suspend fun update(student: StudentEntity)

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("DELETE FROM student WHERE id = :id")
    suspend fun deleteById(id: Int)
}
