package com.ville.assistedreminders.data.entity.room

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Reminder
import java.util.*

class ReminderToAccount {
    @Embedded
    lateinit var reminder: Reminder

    @Relation(parentColumn = "creator_id", entityColumn = "id")
    lateinit var accounts: List<Account>

    @get:Ignore
    val account: Account
        get() = accounts[0]

    /**
     * Allow this class to be destroyed by consumers
     */
    operator fun component1() = reminder
    operator fun component2() = account

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is ReminderToAccount -> reminder == other.reminder && accounts == other.accounts
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(reminder, accounts)
}