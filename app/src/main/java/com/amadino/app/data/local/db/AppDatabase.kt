package com.amadino.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amadino.app.data.local.converter.Converters
import com.amadino.app.data.local.dao.AttendanceDao
import com.amadino.app.data.local.dao.ClassTimeDao
import com.amadino.app.data.local.dao.PaymentDao
import com.amadino.app.data.local.dao.ShiftDao
import com.amadino.app.data.local.dao.SmsCounterDao
import com.amadino.app.data.local.dao.SmsLogDao
import com.amadino.app.data.local.dao.SmsTemplateDao
import com.amadino.app.data.local.dao.StudentDao
import com.amadino.app.data.local.dao.WeekDayDao
import com.amadino.app.data.local.entity.AttendanceEntity
import com.amadino.app.data.local.entity.ClassTimeEntity
import com.amadino.app.data.local.entity.PaymentEntity
import com.amadino.app.data.local.entity.SmsCounterEntity
import com.amadino.app.data.local.entity.SmsLogEntity
import com.amadino.app.data.local.entity.SmsTemplateEntity
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
        PaymentEntity::class,
        SmsTemplateEntity::class
    ],
    version = 3,
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
    abstract fun smsTemplateDao(): SmsTemplateDao
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
                    .addCallback(SeedCallback)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * Adds the user-editable SMS template table. Existing rows are kept; the
         * templates simply fall back to the built-in defaults until edited.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `sms_template` (" +
                        "`event_type` TEXT NOT NULL, " +
                        "`body` TEXT NOT NULL, " +
                        "`updated_at` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`event_type`))"
                )
            }
        }

        /**
         * Adds the delivery-report column. Existing rows keep `NULL`, meaning
         * "no report available" for messages sent before this version.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE `sms_log` ADD COLUMN `delivery_status` TEXT")
            }
        }

        /**
         * Inserts the seven week days on first database creation. Saturday to
         * Wednesday are school days by default; Thursday and Friday are not.
         * The user can change the flags later in settings.
         */
        private val SeedCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val days = listOf(
                    Triple("شنبه", "SAT", 1),
                    Triple("یکشنبه", "SUN", 1),
                    Triple("دوشنبه", "MON", 1),
                    Triple("سه‌شنبه", "TUE", 1),
                    Triple("چهارشنبه", "WED", 1),
                    Triple("پنجشنبه", "THU", 0),
                    Triple("جمعه", "FRI", 0)
                )
                days.forEachIndexed { index, (name, code, isSchoolDay) ->
                    db.execSQL(
                        "INSERT INTO week_day (id, name, code, is_school_day, sort_order) " +
                            "VALUES (${index + 1}, '$name', '$code', $isSchoolDay, ${index + 1})"
                    )
                }
                db.execSQL(
                    "INSERT INTO sms_counter (id, total_sent, total_failed, total_skipped, last_updated) " +
                        "VALUES (1, 0, 0, 0, ${System.currentTimeMillis()})"
                )
            }
        }
    }
}
