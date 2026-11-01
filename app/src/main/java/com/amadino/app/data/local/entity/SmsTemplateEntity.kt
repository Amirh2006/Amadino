package com.amadino.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * User-editable SMS template, one row per [com.amadino.app.domain.model.SmsEventType].
 * A missing row means "use the built-in default".
 */
@Entity(tableName = "sms_template")
data class SmsTemplateEntity(
    @PrimaryKey
    @ColumnInfo(name = "event_type")
    val eventType: String, // ENTRY / EXIT

    @ColumnInfo(name = "body")
    val body: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
