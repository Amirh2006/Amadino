package com.amadino.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.amadino.app.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Query(
        """SELECT * FROM attendance
           WHERE class_time_id = :classTimeId
             AND attendance_date = :attendanceDate
           ORDER BY student_id ASC"""
    )
    fun getByClassTimeAndDate(classTimeId: Int, attendanceDate: Long): Flow<List<AttendanceEntity>>

    @Query(
        """SELECT * FROM attendance
           WHERE student_id = :studentId
             AND class_time_id = :classTimeId
             AND attendance_date = :attendanceDate"""
    )
    fun getByStudentClassTimeDate(
        studentId: Int,
        classTimeId: Int,
        attendanceDate: Long
    ): Flow<AttendanceEntity?>

    @Query(
        """SELECT * FROM attendance
           WHERE student_id = :studentId
           ORDER BY attendance_date DESC, class_time_id ASC"""
    )
    fun getByStudent(studentId: Int): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg attendances: AttendanceEntity): List<Long>

    @Update
    suspend fun update(attendance: AttendanceEntity)

    @Delete
    suspend fun delete(attendance: AttendanceEntity)
}
