package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sms_log",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AttendanceEntity::class,
            parentColumns = ["id"],
            childColumns = ["attendance_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["student_id"]),
        Index(value = ["attendance_id"]),
        Index(value = ["event_type"]),
        Index(value = ["sent_at"])
    ]
)
data class SmsLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "attendance_id")
    val attendanceId: Int? = null,

    @ColumnInfo(name = "event_type")
    val eventType: String, // ENTRY / EXIT

    @ColumnInfo(name = "destination")
    val destination: String,

    @ColumnInfo(name = "message_body")
    val messageBody: String,

    @ColumnInfo(name = "status")
    val status: String, // SUCCESS / FAILED / SKIPPED / PENDING

    @ColumnInfo(name = "error_reason")
    val errorReason: String? = null,

    @ColumnInfo(name = "sent_at")
    val sentAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "fallback_used")
    val fallbackUsed: Boolean = false
)
