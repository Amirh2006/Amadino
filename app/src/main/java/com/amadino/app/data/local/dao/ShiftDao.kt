package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {

    @Query("SELECT * FROM shift WHERE day_id = :dayId ORDER BY start_time ASC")
    fun getByDay(dayId: Int): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shift WHERE id = :id")
    fun getById(id: Int): Flow<ShiftEntity?>

    @Query("SELECT * FROM shift WHERE day_id = :dayId AND is_active = 1 ORDER BY start_time ASC")
    fun getActiveByDay(dayId: Int): Flow<List<ShiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shift: ShiftEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg shifts: ShiftEntity): List<Long>

    @Update
    suspend fun update(shift: ShiftEntity)

    @Delete
    suspend fun delete(shift: ShiftEntity)

    @Query("DELETE FROM shift WHERE id = :id")
    suspend fun deleteById(id: Int)
}
