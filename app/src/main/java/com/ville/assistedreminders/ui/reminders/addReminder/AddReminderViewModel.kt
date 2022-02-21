package com.ville.assistedreminders.ui.reminders.addReminder

import androidx.lifecycle.ViewModel
import androidx.work.*
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.Graph.accountRepository
import com.ville.assistedreminders.Graph.notificationRepository
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import com.ville.assistedreminders.util.*
import java.util.*

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
): ViewModel() {
    suspend fun getLoggedInAccount(): Account? {
        return accountRepository.getLoggedInAccount()
    }

    suspend fun saveReminder(reminder: Reminder, scheduling: Calendar, scheduleNotification: Boolean) {
        // Immediately notify for a new reminder
        notifyNewReminder(reminder)

        // Schedule a notification in the future if notify checkbox was checked
        // and the given time hasn't already passed
        val newReminderId = reminderRepository.addReminder(reminder)
        if (scheduleNotification && reminder.reminder_time.time > System.currentTimeMillis()) {
            val newNotificationID = notificationRepository.addNotification(
                Notification(
                    notificationTime = scheduling.time,
                    reminder_id = newReminderId
                )
            )
            scheduleReminderNotification(newNotificationID, reminder, scheduling)
        }
    }

    init {
        createNotificationChannel(context = Graph.appContext)
    }
}

data class ReminderViewState(
    val textFromSpeech: String? = null,
)
