package com.amadino.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.amadino.app.data.local.converter.Converters
import com.amadino.app.data.local.dao.AttendanceDao
import com.amadino.app.data.local.dao.ClassTimeDao
import com.amadino.app.data.local.dao.PaymentDao
import com.amadino.app.data.local.dao.ShiftDao
import com.amadino.app.data.local.dao.SmsCounterDao
import com.amadino.app.data.local.dao.SmsLogDao
import com.amadino.app.data.local.dao.StudentDao
import com.amadino.app.data.local.dao.WeekDayDao
import com.amadino.app.data.local.entity.AttendanceEntity
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.local.entity.PaymentEntity
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.local.entity.ShiftEntity
import com.amadino.app.data.local.entity.StudentEntity
import com.amadino.app.data.local.entity.WeekDayEntity

@Database(
    entities = [
        WeekDayEntity::class,
        ShiftEntity::class,
        ClassTimeEntity::class,
        StudentEntity::class,
        AttendanceEntity::class,
        SmsLogEntity::class,
        SmsCounterEntity::class,
        PaymentEntity::class
    ],
    version = 1,
    autoMigrations = []
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weekDayDao(): WeekDayDao
    abstract fun shiftDao(): ShiftDao
    abstract fun classTimeDao(): ClassTimeDao
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun smsLogDao(): SmsLogDao
    abstract fun smsCounterDao(): SmsCounterDao
    abstract fun paymentDao(): PaymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "amadino_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
