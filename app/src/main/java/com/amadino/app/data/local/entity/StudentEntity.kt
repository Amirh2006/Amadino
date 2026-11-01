package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "student",
    indices = [
        Index(value = ["national_id"], unique = true)
    ]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "father_name")
    val fatherName: String,

    @ColumnInfo(name = "national_id")
    val nationalId: String? = null,

    @ColumnInfo(name = "grade")
    val grade: String,

    @ColumnInfo(name = "mobile1")
    val mobile1: String,

    @ColumnInfo(name = "mobile2")
    val mobile2: String? = null,

    @ColumnInfo(name = "total_tuition")
    val totalTuition: Long = 0,

    @ColumnInfo(name = "current_debt")
    val currentDebt: Long = 0,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
