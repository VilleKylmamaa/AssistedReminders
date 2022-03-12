package com.ville.assistedreminders.ui.reminders


import android.location.Location
import androidx.compose.runtime.MutableState
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
import com.ville.assistedreminders.ui.MainActivity
import com.ville.assistedreminders.util.scheduleReminderNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class ReminderListViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val mainActivity: MainActivity,
    private val currentLocation: MutableState<Location?>
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

    private suspend fun listRemindersBasedOnSchedulingAndLocation() {
        mainActivity.getCurrentLocation()
        currentLocation.value

        val loggedInAccount = getLoggedInAccount()
        if (loggedInAccount != null) {
            // As a default, show reminders of which scheduling has passed
            reminderRepository.getRemindersBefore(
                loggedInAccount.accountId,
                Calendar.getInstance().time
            ).collect { dueRemindersList ->
                val nearRemindersList = mutableListOf<ReminderToAccount>()
                for (item in dueRemindersList) {

                    val reminder = item.reminder
                    val reminderLatitude = reminder.location_x
                    val reminderLongitude = reminder.location_y

                    // Latitude 0.0 and longitude 0.0 means that the reminder is not
                    // location based and should be displayed based on its scheduling
                    if (reminderLatitude == 0.0 && reminderLongitude == 0.0) {
                        nearRemindersList.add(item)
                    } else {
                        val results = FloatArray(1)
                        currentLocation.value?.let {
                            Location.distanceBetween(
                                it.latitude, it.longitude,
                                reminderLatitude, reminderLongitude,
                                results
                            )
                        }
                        val distance = results[0]

                        // Show reminder if the user is within 200m of the set location
                        if (distance <= 200) {
                            nearRemindersList.add(item)
                        }
                    }
                }
                _state.value = ReminderListViewState(
                    remindersForAccount = MutableLiveData(nearRemindersList)
                )
            }
        }
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
                listRemindersBasedOnSchedulingAndLocation()
            }
        }
    }

    init {
        viewModelScope.launch {
            listRemindersBasedOnSchedulingAndLocation()
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
                        location_x = 0.0,
                        location_y = 0.0,
                        reminder_time = resultDate1,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "SportsEsports"
                    ),
                    Reminder(
                        message = "Walk the dog",
                        location_x = 0.0,
                        location_y = 0.0,
                        reminder_time = resultDate2,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "Pets"
                    ),
                    Reminder(
                        message = "Meditate",
                        location_x = 0.0,
                        location_y = 0.0,
                        reminder_time = resultDate3,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "SelfImprovement"
                    ),
                    Reminder(
                        message = "Buy groceries",
                        location_x = 64.99354344655231,
                        location_y = 25.46157945426095,
                        reminder_time = resultDate4,
                        creation_time = Calendar.getInstance().time,
                        creator_id = loggedInAccount.accountId,
                        reminder_seen = false,
                        icon = "ShoppingCart"
                    ),
                    Reminder(
                        message = "Graduate",
                        location_x = 65.05911402553694,
                        location_y = 25.467460624353272,
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