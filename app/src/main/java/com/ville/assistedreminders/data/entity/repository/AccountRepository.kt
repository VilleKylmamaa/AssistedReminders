package com.ville.assistedreminders.data.entity.repository

import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.room.AccountDao

/**
 * A data repository for Account instances
 */
class AccountRepository(
    private val accountDao: AccountDao
) {
    suspend fun addAccount(account: Account) = accountDao.insert(account)
    suspend fun findAccount(username: String) = accountDao.findAccount(username)
    suspend fun getAccountCount(): Int = accountDao.getAccountCount()
    suspend fun getLoggedInAccount(): Account? = accountDao.getLoggedInAccount()
    suspend fun updateAccount(account: Account) = accountDao.update(account)
}