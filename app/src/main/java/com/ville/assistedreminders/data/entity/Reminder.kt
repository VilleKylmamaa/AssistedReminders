package com.ville.assistedreminders.data.entity

import androidx.room.*
import java.util.*

@Entity(
    tableName = "reminders",
    indices = [
        Index("id", unique = true),
        Index("creator_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = [ "id" ],
            childColumns = ["creator_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val reminderId: Long = 0,
    @ColumnInfo(name = "message") var message: String,
    @ColumnInfo(name = "location_x") var location_x: String,
    @ColumnInfo(name = "location_y") var location_y: String,
    @ColumnInfo(name = "reminder_time") var reminder_time: Date,
    @ColumnInfo(name = "creation_time") var creation_time: Date,
    @ColumnInfo(name = "creator_id") var creator_id: Long,
    @ColumnInfo(name = "reminder_seen") var reminder_seen: Boolean,
    @ColumnInfo(name = "icon") var icon: String
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}