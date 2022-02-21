package com.ville.assistedreminders.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.R
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.MainActivity
import com.ville.assistedreminders.ui.reminders.formatToString
import java.util.*
import java.util.concurrent.TimeUnit


fun notifyNewReminder(reminder: Reminder) {
    val appContext = Graph.appContext
    val notificationId = 0

    val intent = Intent(appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        appContext,
        0,
        intent,
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Reminder created successfully")
        .setContentText("Message: ${reminder.message}" +
                "\nDue: ${reminder.reminder_time.formatToString()}"
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        notify(notificationId, builder.build())
    }
}

fun scheduleReminderNotification(notificationId: Long, reminder: Reminder, notificationTime: Calendar) {
    val data: Data = Data.Builder()
        .putString("NOTIFICATION_MESSAGE", reminder.message)
        .putString("NOTIFICATION_DUE", reminder.reminder_time.formatToString())
        .putLong("NOTIFICATION_ID", notificationId)
        .build()
    val timeUntilNotification = notificationTime.timeInMillis - System.currentTimeMillis()
    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(timeUntilNotification, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .build()

    val workManager = WorkManager.getInstance(Graph.appContext)
    workManager.enqueue(notificationWorker)
}

fun createReminderNotification(message: String, dueDate: String, reminderId: Long) {
    val appContext = Graph.appContext

    val intent = Intent(appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        appContext,
        0,
        intent,
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Reminder")
        .setContentText("Message: $message\nDue: $dueDate")
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(appContext)) {
        notify(reminderId.toInt(), builder.build())
    }
}

fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "NotificationChannel"
        val descriptionText = "Channel for handling notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}