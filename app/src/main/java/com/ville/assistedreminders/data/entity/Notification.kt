package com.ville.assistedreminders.data.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "notifications",
    indices = [
        Index("id", unique = true),
        Index("reminder_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Reminder::class,
            parentColumns = [ "id" ],
            childColumns = ["reminder_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Notification(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val notificationId: Long = 0,
    @ColumnInfo(name = "notificationTime") var notificationTime: Date,
    @ColumnInfo(name = "reminder_id") var reminder_id: Long,
)

/*
class NotificationConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}*/