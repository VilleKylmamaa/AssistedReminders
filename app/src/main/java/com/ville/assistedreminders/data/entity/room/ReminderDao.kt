package com.ville.assistedreminders.data.entity.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ville.assistedreminders.data.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReminderDao {
    @Query("SELECT * FROM reminders")
    abstract fun reminders(): Flow<List<Reminder>>

    @Query("SELECT COUNT(reminder_title) FROM reminders LIMIT 1")
    abstract suspend fun getReminderCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Reminder): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Reminder)

    @Delete
    abstract suspend fun delete(entity: Reminder): Int
}