package com.amadino.app.data.local.converter

import androidx.room.TypeConverter

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class Converters {

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun timestampToDate(timestamp: Long?): Date? = timestamp?.let { Date(it) }

    @TypeConverter
    fun instantToEpochMillis(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(millis: Long?): Instant? = millis?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun zoneIdToString(zone: ZoneId?): String? = zone?.id

    @TypeConverter
    fun stringToZoneId(id: String?): ZoneId? = id?.let { ZoneId.of(it) }

    @TypeConverter
    fun zonedDateTimeToEpochMillis(zdt: ZonedDateTime?): Long? = zdt?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun epochMillisToZonedDateTime(millis: Long?): ZonedDateTime? =
        millis?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()) }
}
