package com.ville.assistedreminders.data.entity.room

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.Reminder
import java.util.*

class NotificationToReminder {
    @Embedded
    lateinit var notification: Notification

    @Relation(parentColumn = "reminder_id", entityColumn = "id")
    lateinit var reminders: List<Reminder>

    @get:Ignore
    val reminder: Reminder
        get() = reminders[0]

    /**
     * Allow this class to be destroyed by consumers
     */
    operator fun component1() = notification
    operator fun component2() = reminder

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is NotificationToReminder -> notification == other.notification && reminders == other.reminders
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(notification, reminders)
}