package com.ville.assistedreminders.data.entity.room

import androidx.room.*
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Reminder
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
abstract class ReminderDao {
    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    abstract suspend fun getReminder(id: Long): Reminder?

    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN accounts ON reminders.creator_id = accounts.id
        WHERE creator_id = :accountId
        ORDER BY reminder_time ASC
    """)
    abstract fun getRemindersForAccount(accountId: Long): Flow<List<ReminderToAccount>>

    @Query("""
        SELECT reminders.* FROM reminders
        INNER JOIN accounts ON reminders.creator_id = accounts.id
        WHERE creator_id = :accountId AND reminder_time < :time
        ORDER BY reminder_time ASC
    """)
    abstract fun getRemindersBefore(accountId: Long, time: Date): Flow<List<ReminderToAccount>>

    @Query("SELECT COUNT(id) FROM reminders LIMIT 1")
    abstract suspend fun getReminderCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Reminder): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Reminder)

    @Delete
    abstract suspend fun delete(entity: Reminder): Int
}