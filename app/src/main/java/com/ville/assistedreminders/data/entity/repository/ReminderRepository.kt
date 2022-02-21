package com.ville.assistedreminders.data.entity.repository

import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.room.ReminderDao
import com.ville.assistedreminders.data.entity.room.ReminderToAccount
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * A data repository for Reminder instances
 */
class ReminderRepository(
    private val reminderDao: ReminderDao
) {
    suspend fun getReminder(id: Long): Reminder? = reminderDao.getReminder(id)
    fun getRemindersForAccount(accountId: Long):
        Flow<List<ReminderToAccount>> = reminderDao.getRemindersForAccount(accountId)
    fun getRemindersBefore(accountId: Long, time: Date):
        Flow<List<ReminderToAccount>> = reminderDao.getRemindersBefore(accountId, time)
    suspend fun getReminderCount(): Int = reminderDao.getReminderCount()
    suspend fun addReminder(reminder: Reminder) = reminderDao.insert(reminder)
    suspend fun updateReminder(reminder: Reminder) = reminderDao.update(reminder)
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.delete(reminder)
}