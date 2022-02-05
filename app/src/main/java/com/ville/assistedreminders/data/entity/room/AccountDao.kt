package com.ville.assistedreminders.data.entity.room

import androidx.room.*
import com.ville.assistedreminders.data.entity.Account
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Query("SELECT * FROM accounts")
    abstract fun accounts(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE username = :username")
    abstract suspend fun findAccount(username: String): Account?

    @Query("SELECT COUNT(username) FROM accounts LIMIT 1")
    abstract suspend fun getAccountCount(): Int

    @Query("SELECT * FROM accounts WHERE isLoggedIn='1'")
    abstract suspend fun getLoggedInAccount(): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(entity: Account): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun update(entity: Account)

    @Delete
    abstract suspend fun delete(entity: Account): Int
}