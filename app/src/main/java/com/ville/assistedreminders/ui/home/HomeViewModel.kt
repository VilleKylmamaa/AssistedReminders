package com.ville.assistedreminders.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.AccountRepository
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class HomeViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository,
    private val accountRepository: AccountRepository = Graph.accountRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeViewState())

    val state: StateFlow<HomeViewState>
        get() = _state

    suspend fun updateAccount(account: Account) {
        return accountRepository.updateAccount(account)
    }

    suspend fun getLoggedInAccount(): Account? {
        return accountRepository.getLoggedInAccount()
    }

    suspend fun getLoggedInAccountUsername(): String {
        val account = accountRepository.getLoggedInAccount()
        if (account != null) {
            return account.username
        }
        return ""
    }

    init {
        viewModelScope.launch {
            val loggedInAccount = accountRepository.getLoggedInAccount()

            reminderRepository.reminders().collect { list ->
                _state.value = HomeViewState(
                    loggedInAccount = loggedInAccount,
                    reminders = list
                )
            }
        }
    }
}

data class HomeViewState(
    val loggedInAccount: Account? = null,
    val reminders: List<Reminder> = emptyList()
)