package com.amadino.app.data.repository

import com.amadino.app.data.local.dao.AttendanceDao
import com.amadino.app.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

class AttendanceRepository private constructor(
    private val attendanceDao: AttendanceDao
) {

    fun getByClassTimeAndDate(classTimeId: Int, attendanceDate: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getByClassTimeAndDate(classTimeId, attendanceDate)

    fun getByDate(attendanceDate: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getByDate(attendanceDate)

    fun getByStudentClassTimeDate(
        studentId: Int,
        classTimeId: Int,
        attendanceDate: Long
    ): Flow<AttendanceEntity?> =
        attendanceDao.getByStudentClassTimeDate(studentId, classTimeId, attendanceDate)

    fun getByStudent(studentId: Int): Flow<List<AttendanceEntity>> =
        attendanceDao.getByStudent(studentId)

    suspend fun insertAttendance(attendance: AttendanceEntity): Long =
        attendanceDao.insert(attendance)

    suspend fun insertAll(vararg attendances: AttendanceEntity): List<Long> =
        attendanceDao.insertAll(*attendances)

    suspend fun updateAttendance(attendance: AttendanceEntity) =
        attendanceDao.update(attendance)

    companion object {
        @Volatile
        private var INSTANCE: AttendanceRepository? = null

        fun getInstance(attendanceDao: AttendanceDao): AttendanceRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = AttendanceRepository(attendanceDao)
                INSTANCE!!
            }
        }
    }
}
