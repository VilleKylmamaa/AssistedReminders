package com.ville.assistedreminders.data.entity.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Reminder

/*
 * The [RoomDatabase] for this app
 */
@Database(
    entities = [
        Reminder::class,
        Account::class
    ],
    version = 6,
    exportSchema = false
)

abstract class AssistedRemindersDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var instance: AssistedRemindersDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AssistedRemindersDatabase::class.java,
                "MyDatabase.db"
            ).build()
    }
}