package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ClassTimeEntity::class,
            parentColumns = ["id"],
            childColumns = ["class_time_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["shift_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WeekDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["day_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = ["student_id", "class_time_id", "attendance_date"],
            unique = true
        ),
        Index(value = ["class_time_id"]),
        Index(value = ["shift_id"]),
        Index(value = ["day_id"]),
        Index(value = ["attendance_date"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "class_time_id")
    val classTimeId: Int,

    @ColumnInfo(name = "shift_id")
    val shiftId: Int,

    @ColumnInfo(name = "day_id")
    val dayId: Int,

    @ColumnInfo(name = "attendance_date")
    val attendanceDate: Long,

    @ColumnInfo(name = "status")
    val status: String, // PRESENT / ABSENT / LATE

    @ColumnInfo(name = "recorded_at")
    val recordedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "recorded_by")
    val recordedBy: String? = null
)
