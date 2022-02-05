package com.ville.assistedreminders.data.entity.repository

import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.room.ReminderDao
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for Reminder instances
 */
class ReminderRepository(
    private val reminderDao: ReminderDao
) {
    fun reminders(): Flow<List<Reminder>> = reminderDao.reminders()
    suspend fun getReminderCount(): Int = reminderDao.getReminderCount()
    suspend fun addReminder(reminder: Reminder) = reminderDao.insert(reminder)
}