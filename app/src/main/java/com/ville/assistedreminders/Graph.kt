package com.ville.assistedreminders

import android.content.Context
import androidx.room.Room
import com.ville.assistedreminders.data.entity.repository.AccountRepository
import com.ville.assistedreminders.data.entity.repository.NotificationRepository
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import com.ville.assistedreminders.data.entity.room.AssistedRemindersDatabase

/**
 * A simple singleton dependency graph
 * For a real app, please use something like Koin/Dagger/Hilt instead
 */
object Graph {
    lateinit var database: AssistedRemindersDatabase
        private set

    lateinit var appContext: Context

    val reminderRepository by lazy {
        ReminderRepository(
            reminderDao = database.reminderDao()
        )
    }
    val accountRepository by lazy {
        AccountRepository(
            accountDao = database.accountDao()
        )
    }
    val notificationRepository by lazy {
        NotificationRepository(
            notificationDao = database.notificationDao()
        )
    }

    fun provide(context: Context) {
        appContext = context
        database = Room.databaseBuilder(context, AssistedRemindersDatabase::class.java, "data.db")
            .fallbackToDestructiveMigration() // Don't use this in a production app, it will remove everything
            .build()
    }
}