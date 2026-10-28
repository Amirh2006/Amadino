package com.amadino.app.data.repository

import com.amadino.app.data.local.dao.ClassTimeDao
import com.amadino.app.data.local.dao.ShiftDao
import com.amadino.app.data.local.dao.WeekDayDao
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.WeekDayEntity
import kotlinx.coroutines.flow.Flow

class ShiftRepository private constructor(
    private val weekDayDao: WeekDayDao,
    private val shiftDao: ShiftDao,
    private val classTimeDao: ClassTimeDao
) {

    fun getWeekDays(): Flow<List<WeekDayEntity>> = weekDayDao.getAll()
    fun getSchoolDays(): Flow<List<WeekDayEntity>> = weekDayDao.getSchoolDays()
    fun getWeekDayById(id: Int): Flow<WeekDayEntity?> = weekDayDao.getById(id)

    fun getShiftsByDay(dayId: Int): Flow<List<ShiftEntity>> = shiftDao.getByDay(dayId)
    fun getActiveShiftsByDay(dayId: Int): Flow<List<ShiftEntity>> = shiftDao.getActiveByDay(dayId)
    fun getShiftById(id: Int): Flow<ShiftEntity?> = shiftDao.getById(id)

    fun getClassTimesByShift(shiftId: Int): Flow<List<ClassTimeEntity>> =
        classTimeDao.getByShift(shiftId)

    suspend fun insertWeekDay(weekDay: WeekDayEntity): Long = weekDayDao.insert(weekDay)
    suspend fun insertShift(shift: ShiftEntity): Long = shiftDao.insert(shift)
    suspend fun insertClassTime(classTime: ClassTimeEntity): Long = classTimeDao.insert(classTime)

    suspend fun updateWeekDay(weekDay: WeekDayEntity) = weekDayDao.update(weekDay)
    suspend fun updateShift(shift: ShiftEntity) = shiftDao.update(shift)
    suspend fun updateClassTime(classTime: ClassTimeEntity) = classTimeDao.update(classTime)

    suspend fun deleteWeekDay(weekDay: WeekDayEntity) = weekDayDao.delete(weekDay)
    suspend fun deleteShift(shift: ShiftEntity) = shiftDao.delete(shift)
    suspend fun deleteClassTime(classTime: ClassTimeEntity) = classTimeDao.delete(classTime)

    companion object {
        @Volatile
        private var INSTANCE: ShiftRepository? = null

        fun getInstance(
            weekDayDao: WeekDayDao,
            shiftDao: ShiftDao,
            classTimeDao: ClassTimeDao
        ): ShiftRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE = ShiftRepository(weekDayDao, shiftDao, classTimeDao)
                INSTANCE!!
            }
        }
    }
}
