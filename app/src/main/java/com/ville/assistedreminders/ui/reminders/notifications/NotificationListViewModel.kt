package com.ville.assistedreminders.ui.reminders.notifications

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.NotificationRepository
import com.ville.assistedreminders.data.entity.room.NotificationToReminder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificationListViewModel(
    private val notificationRepository: NotificationRepository = Graph.notificationRepository,
    reminder: Reminder
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationListViewState())

    val state: StateFlow<NotificationListViewState>
        get() = _state

    suspend fun deleteNotification(notification: Notification) {
        notificationRepository.deleteNotification(notification)
    }

    fun updateNotificationList(reminder: Reminder) {
        viewModelScope.launch {
            notificationRepository.getNotificationsForReminder(reminder.reminderId)
                .collect { list ->
                    _state.value = NotificationListViewState(
                        notificationsForReminder = MutableLiveData(list)
                    )
                }
        }
    }

    init {
        viewModelScope.launch {
            notificationRepository.getNotificationsForReminder(reminder.reminderId)
                .collect { list ->
                    _state.value = NotificationListViewState(
                        notificationsForReminder = MutableLiveData(list)
                    )
                }
        }
    }

}

data class NotificationListViewState(
    val notificationsForReminder: MutableLiveData<List<NotificationToReminder>> = MutableLiveData(emptyList())
)