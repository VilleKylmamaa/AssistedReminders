package com.ville.assistedreminders.ui.reminders.addReminder

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.Graph.accountRepository
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import com.ville.assistedreminders.ui.MainActivity
import java.util.*

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
): ViewModel() {
    suspend fun getLoggedInAccount(): Account? {
        return accountRepository.getLoggedInAccount()
    }

    suspend fun saveReminder(reminder: Reminder): Long {
        return reminderRepository.addReminder(reminder)
    }
}

data class ReminderViewState(
    val textFromSpeech: String? = null,
)