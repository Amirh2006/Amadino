package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sms_counter")
data class SmsCounterEntity(
    @PrimaryKey
    val id: Int = 1, // single-row counter; always id = 1

    @ColumnInfo(name = "total_sent")
    val totalSent: Long = 0,

    @ColumnInfo(name = "total_failed")
    val totalFailed: Long = 0,

    @ColumnInfo(name = "total_skipped")
    val totalSkipped: Long = 0,

    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)
