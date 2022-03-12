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
import com.google.android.gms.maps.model.LatLng
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.R
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.MainActivity
import com.ville.assistedreminders.ui.reminders.formatToString
import java.util.*
import java.util.concurrent.TimeUnit


fun notifyNewReminder(reminder: Reminder) {
    val appContext = Graph.appContext
    val notificationId = 99999
    val message = "Message: ${reminder.message}" +
            "\nDue: ${reminder.reminder_time.formatToString()}" +
            "\nLatitude: ${reminder.location_x}" +
            "\nLongitude: ${reminder.location_y}"

    // Make notification clickable
    val intent = Intent(appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        appContext,
        0,
        intent,
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val notificationBuilder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Reminder Created Successfully")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        notify(notificationId, notificationBuilder.build())
    }
}

fun scheduleReminderNotification(notificationId: Long, reminder: Reminder, notificationTime: Calendar) {
    val message = "Message: ${reminder.message}" +
            "\nDue: ${reminder.reminder_time.formatToString()}"

    val data: Data = Data.Builder()
        .putString("NOTIFICATION_MESSAGE", message)
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

fun notifyReminder(message: String, reminderId: Long) {
    val appContext = Graph.appContext

    // Make notification clickable
    val intent = Intent(appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        appContext,
        0,
        intent,
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val notificationBuilder = NotificationCompat.Builder(appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Scheduled Reminder")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(appContext)) {
        notify(reminderId.toInt(), notificationBuilder.build())
    }
}

fun notifyGeofence(title: String, message: String, notificationId: Long) {
    val appContext = Graph.appContext

    // Make notification clickable
    val intent = Intent(appContext, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        appContext,
        0,
        intent,
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    val notificationBuilder = NotificationCompat.Builder(appContext.applicationContext,
        "LOCATION_NOTIFICATION_CHANNEL")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(NotificationCompat.BigTextStyle().bigText(message))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    createNotificationChannel(appContext, "LOCATION_NOTIFICATION_CHANNEL")
    notificationManager.notify(notificationId.toInt(), notificationBuilder.build())
}

fun scheduleGeofence(
    notificationId: Long,
    reminder: Reminder,
    notificationTime: Calendar,
    location: LatLng
) {
    val title = "Timed Location Reminder"
    val message = "Message: ${reminder.message}" +
            "\n\nDue: ${reminder.reminder_time.formatToString()}" +
            "\n\nLatitude: ${location.latitude}" +
            "\nLongitude: ${location.longitude}"

    val data: Data = Data.Builder()
        .putString("NOTIFICATION_TITLE", title)
        .putString("NOTIFICATION_MESSAGE", message)
        .putString("NOTIFICATION_LOCATION", location.toString())
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

fun createNotificationChannel(context: Context, channelId: String) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "NotificationChannel"
        val descriptionText = "Channel for handling notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        // register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}