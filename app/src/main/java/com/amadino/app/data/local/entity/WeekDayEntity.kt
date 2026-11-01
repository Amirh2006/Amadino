package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "week_day")
data class WeekDayEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name", index = true)
    val name: String,

    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "is_school_day")
    val isSchoolDay: Boolean = true,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)
