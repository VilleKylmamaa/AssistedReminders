package com.ville.assistedreminders.data.entity.repository

import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.room.ReminderDao
import com.ville.assistedreminders.data.entity.room.ReminderToAccount
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for Reminder instances
 */
class ReminderRepository(
    private val reminderDao: ReminderDao
) {
    fun getRemindersForAccount(accountId: Long):
        Flow<List<ReminderToAccount>> = reminderDao.getRemindersForAccount(accountId)
    suspend fun getReminderCount(): Int = reminderDao.getReminderCount()
    suspend fun addReminder(reminder: Reminder) = reminderDao.insert(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.update(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.delete(reminder)
}