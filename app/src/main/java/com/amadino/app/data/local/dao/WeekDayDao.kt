package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.WeekDayEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeekDayDao {

    @Query("SELECT * FROM week_day ORDER BY sort_order ASC, name ASC")
    fun getAll(): Flow<List<WeekDayEntity>>

    @Query("SELECT * FROM week_day WHERE id = :id")
    fun getById(id: Int): Flow<WeekDayEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weekDay: WeekDayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg weekDays: WeekDayEntity): List<Long>

    @Update
    suspend fun update(weekDay: WeekDayEntity)

    @Delete
    suspend fun delete(weekDay: WeekDayEntity)

    @Query("DELETE FROM week_day WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM week_day WHERE is_school_day = 1 ORDER BY sort_order ASC")
    fun getSchoolDays(): Flow<List<WeekDayEntity>>
}
