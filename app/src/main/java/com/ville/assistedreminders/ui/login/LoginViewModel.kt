package com.ville.assistedreminders.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val accountRepository: AccountRepository = Graph.accountRepository
): ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun findAccount(username: String): Account? {
        return accountRepository.findAccount(username)
    }

    suspend fun updateAccount(account: Account) {
        accountRepository.updateAccount(account)
    }

    suspend fun addAccount(account: Account) {
        accountRepository.addAccount(account)
    }

    init {
        viewModelScope.launch {
            // Check that all accounts are logged out to avoid multiple accounts
            // logged in at the same time
            while(accountRepository.getLoggedInAccount() != null){
                val accountLoggedIn = accountRepository.getLoggedInAccount()
                if (accountLoggedIn != null) {
                    accountLoggedIn.isLoggedIn = false
                    accountRepository.updateAccount(accountLoggedIn)

                }
            }

            // Create an account with username "admin" and password "admin"
            // if there are no accounts yet
            if (accountRepository.getAccountCount() < 1) {
                accountRepository.addAccount(Account(
                    username = "admin",
                    password = "admin",
                    email = "admin@admin.com"
                    )
                )
            }
        }
    }
}

data class ReminderViewState(
    val reminders: List<Reminder> = emptyList()
)