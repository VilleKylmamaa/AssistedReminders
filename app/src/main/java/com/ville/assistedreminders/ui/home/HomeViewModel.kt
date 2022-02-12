package com.ville.assistedreminders.ui.home

import androidx.lifecycle.ViewModel
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.repository.AccountRepository
import kotlinx.coroutines.flow.*


class HomeViewModel(
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
}

data class HomeViewState(
    val loggedInAccount: Account? = null,
)