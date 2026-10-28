package com.amadino.app.data.repository

import com.amadino.app.data.local.dao.WeekDayDao
import com.amadino.app.data.local.entity.WeekDayEntity
import kotlinx.coroutines.flow.Flow

class StudentRepository private constructor(
    private val studentDao: StudentDao,
    private val weekDayDao: WeekDayDao
) {

    fun getAllStudents(): Flow<List<com.amadino.app.data.local.entity.StudentEntity>> =
        studentDao.getAll()

    fun getActiveStudents(): Flow<List<com.amadino.app.data.local.entity.StudentEntity>> =
        studentDao.getActive()

    fun getStudentById(id: Int): Flow<com.amadino.app.data.local.entity.StudentEntity?> =
        studentDao.getById(id)

    suspend fun insertStudent(student: com.amadino.app.data.local.entity.StudentEntity): Long =
        studentDao.insert(student)

    suspend fun updateStudent(student: com.amadino.app.data.local.entity.StudentEntity) =
        studentDao.update(student)

    suspend fun deleteStudent(student: com.amadino.app.data.local.entity.StudentEntity) =
        studentDao.delete(student)

    companion object {
        @Volatile
        private var INSTANCE: StudentRepository? = null

        fun getInstance(studentDao: StudentDao, weekDayDao: WeekDayDao): StudentRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = StudentRepository(studentDao, weekDayDao)
                INSTANCE!!
            }
        }
    }
}
