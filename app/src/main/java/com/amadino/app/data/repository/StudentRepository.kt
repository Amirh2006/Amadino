package com.amadino.app.data.repository

import com.amadino.app.data.local.dao.StudentDao
import com.amadino.app.data.local.dao.WeekDayDao
import com.amadino.app.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

class StudentRepository private constructor(
    private val studentDao: StudentDao,
    private val weekDayDao: WeekDayDao
) {

    fun getAllStudents(): Flow<List<StudentEntity>> = studentDao.getAll()

    fun getActiveStudents(): Flow<List<StudentEntity>> = studentDao.getActive()

    fun getStudentById(id: Int): Flow<StudentEntity?> = studentDao.getById(id)

    suspend fun insertStudent(student: StudentEntity): Long = studentDao.insert(student)

    suspend fun updateStudent(student: StudentEntity) = studentDao.update(student)

    suspend fun deleteStudent(student: StudentEntity) = studentDao.delete(student)

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
