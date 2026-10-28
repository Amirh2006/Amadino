package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "payment",
    foreignKeys = [
        ForeignKey(
            entity = StudentEntity::class,
            parentColumns = ["id"],
            childColumns = ["student_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["student_id"])]
)
data class PaymentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "amount")
    val amount: Long,

    @ColumnInfo(name = "payment_date")
    val paymentDate: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "recorded_at")
    val recordedAt: Long = System.currentTimeMillis()
)
