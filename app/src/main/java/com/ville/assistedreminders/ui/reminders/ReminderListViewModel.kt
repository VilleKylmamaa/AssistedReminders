package com.ville.assistedreminders.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReminderListViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ReminderListViewState())

    val state: StateFlow<ReminderListViewState>
        get() = _state

    init {
        viewModelScope.launch {
            reminderRepository.reminders().collect { list ->
                _state.value = ReminderListViewState(
                    reminders = list
                )
            }
        }
        addRemindersToDb()
    }

    /*
     * Add 5 reminders to the database if there are none
     */
    private fun addRemindersToDb() {
        val list = mutableListOf(
            Reminder(reminderTitle = "Buy groceries"),
            Reminder(reminderTitle = "Browse memes"),
            Reminder(reminderTitle = "Meditate"),
            Reminder(reminderTitle = "Walk the dog"),
            Reminder(reminderTitle = "Graduate"),
        )

        viewModelScope.launch{
            if (reminderRepository.getReminderCount() < 1) {
                list.forEach{ reminder -> reminderRepository.addReminder(reminder) }
            }
        }
    }
}

data class ReminderListViewState(
    val reminders: List<Reminder> = emptyList()
)