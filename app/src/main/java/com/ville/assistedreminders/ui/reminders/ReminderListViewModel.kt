package com.ville.assistedreminders.ui.reminders


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.Graph.notificationRepository
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import com.ville.assistedreminders.data.entity.room.ReminderToAccount
import com.ville.assistedreminders.util.scheduleReminderNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class ReminderListViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ReminderListViewState())

    val state: StateFlow<ReminderListViewState>
        get() = _state

    suspend fun getLoggedInAccount(): Account? {
        return Graph.accountRepository.getLoggedInAccount()
    }

    suspend fun updateReminder(reminder: Reminder) {
        reminderRepository.updateReminder(reminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        reminderRepository.deleteReminder(reminder)
    }

    suspend fun addNotification(notification: Notification, reminder: Reminder, scheduling: Calendar) {
        val notificationId = notificationRepository.addNotification(notification)
        scheduleReminderNotification(notificationId, reminder, scheduling)
    }

    suspend fun showAllSwitch(showAll: Boolean) {
        val loggedInAccount = getLoggedInAccount()
        if (loggedInAccount != null) {
            if (showAll) {
                reminderRepository.getRemindersForAccount(loggedInAccount.accountId)
                    .collect { list ->
                        _state.value = ReminderListViewState(
                            remindersForAccount = MutableLiveData(list)
                        )
                    }
            } else {
                reminderRepository.getRemindersBefore(loggedInAccount.accountId, Calendar.getInstance().time)
                    .collect { list ->
                        _state.value = ReminderListViewState(
                            remindersForAccount = MutableLiveData(list)
                        )
                    }
            }
        }
    }

    init {
        viewModelScope.launch {
            val loggedInAccount = getLoggedInAccount()
            if (loggedInAccount != null) {
                reminderRepository.getRemindersBefore(loggedInAccount.accountId, Calendar.getInstance().time)
                    .collect { list ->
                        _state.value = ReminderListViewState(
                            remindersForAccount = MutableLiveData(list)
                        )
                    }
            }
        }
        addRemindersToDb()
    }

    /*
     * Add 5 reminders to the database if there are none
     */
    private fun addRemindersToDb() {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 2)
        val resultDate1 = calendar.time
        calendar.add(Calendar.HOUR, 2)
        val resultDate2 = calendar.time
        calendar.add(Calendar.HOUR, 1)
        val resultDate3 = calendar.time
        calendar.add(Calendar.HOUR, 22)
        val resultDate4 = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 500)
        val resultDate5 = calendar.time

        viewModelScope.launch{
            val loggedInAccount = getLoggedInAccount()
                if (loggedInAccount != null) {

                val list = mutableListOf(
                    Reminder(
                        message = "Browse memes",
                        location_x = "65.021545",
                        location_y = "25.469885",
                        reminder_time = resultDate1,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "SportsEsports"
                    ),
                    Reminder(
                        message = "Walk the dog",
                        location_x = "65.021545",
                        location_y = "25.469885",
                        reminder_time = resultDate2,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "Pets"
                    ),
                    Reminder(
                        message = "Meditate",
                        location_x = "65.021545",
                        location_y = "25.469885",
                        reminder_time = resultDate3,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "SelfImprovement"
                    ),
                    Reminder(
                        message = "Buy groceries",
                        location_x = "65.021545",
                        location_y = "25.469885",
                        reminder_time = resultDate4,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "ShoppingCart"
                    ),
                    Reminder(
                        message = "Graduate",
                        location_x = "65.021545",
                        location_y = "25.469885",
                        reminder_time = resultDate5,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "Grade"
                    ),
                )

                if (reminderRepository.getReminderCount() < 1) {
                    list.forEach{ reminder -> reminderRepository.addReminder(reminder) }
                }
            }
        }
    }
}

data class ReminderListViewState(
    val remindersForAccount: MutableLiveData<List<ReminderToAccount>> = MutableLiveData(emptyList())
)