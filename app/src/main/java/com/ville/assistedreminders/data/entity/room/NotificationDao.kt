package com.ville.assistedreminders.data.entity.room

import androidx.room.*
import com.ville.assistedreminders.data.entity.Notification
import kotlinx.coroutines.flow.Flow

@Dao
abstract class NotificationDao {
    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    abstract suspend fun getNotification(id: Long): Notification?

    @Query("""
        SELECT notifications.* FROM notifications
        INNER JOIN reminders ON notifications.reminder_id = reminders.id
        WHERE reminder_id = :reminderId
        ORDER BY notificationTime ASC
    """)
    abstract fun getNotificationsForReminder(reminderId: Long): Flow<List<NotificationToReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Notification): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Notification)

    @Delete
    abstract suspend fun delete(entity: Notification): Int
}