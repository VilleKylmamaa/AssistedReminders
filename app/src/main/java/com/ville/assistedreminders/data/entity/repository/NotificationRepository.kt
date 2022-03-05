package com.ville.assistedreminders.data.entity.repository

import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.room.NotificationDao
import com.ville.assistedreminders.data.entity.room.NotificationToReminder
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for Notification instances
 */
class NotificationRepository(
    private val notificationDao: NotificationDao
) {
    suspend fun getNotification(notificationId: Long) = notificationDao.getNotification(notificationId)
    fun getNotificationsForReminder(reminderId: Long):
        Flow<List<NotificationToReminder>> = notificationDao.getNotificationsForReminder(reminderId)

    suspend fun addNotification(notification: Notification) = notificationDao.insert(notification)
    suspend fun updateNotification(notification: Notification) = notificationDao.update(notification)
    suspend fun deleteNotification(notification: Notification) = notificationDao.delete(notification)
}