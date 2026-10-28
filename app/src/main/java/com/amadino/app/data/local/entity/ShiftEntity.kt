package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shift",
    foreignKeys = [
        ForeignKey(
            entity = WeekDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["day_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["day_id"])]
)
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "day_id")
    val dayId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "start_time")
    val startTime: Long,

    @ColumnInfo(name = "end_time")
    val endTime: Long,

    @ColumnInfo(name = "monitor_name")
    val monitorName: String = "",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true
)
