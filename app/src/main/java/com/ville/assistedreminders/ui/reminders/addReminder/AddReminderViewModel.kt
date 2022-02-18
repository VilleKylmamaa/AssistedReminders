package com.ville.assistedreminders.ui.reminders.addReminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.ville.assistedreminders.R
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.Graph.accountRepository
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import com.ville.assistedreminders.util.NotificationWorker
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
): ViewModel() {
    suspend fun getLoggedInAccount(): Account? {
        return accountRepository.getLoggedInAccount()
    }

    suspend fun saveReminder(reminder: Reminder): Long {
        createReminderNotification(reminder)
        return reminderRepository.addReminder(reminder)
    }

    init {
        createNotificationChannel(context = Graph.appContext)
    }
}

private fun createReminderNotification(reminder: Reminder) {
    val notificationId = 2
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("New reminder added")
        .setContentText(reminder.message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    with(NotificationManagerCompat.from(Graph.appContext)) {
        notify(notificationId, builder.build())
    }
}

private fun setOneTimeNotification() {
    val workManager = WorkManager.getInstance(Graph.appContext)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(10, TimeUnit.SECONDS)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(notificationWorker)

    //Monitoring for state of work
    workManager.getWorkInfoByIdLiveData(notificationWorker.id)
        .observeForever { workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                createSuccessNotification()
            } else {
                createErrorNotification()
            }
        }
}

private fun createSuccessNotification() {
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Success! Download complete")
        .setContentText("Your countdown completed successfully")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}

private fun createErrorNotification() {
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Error")
        .setContentText("Something went wrong!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}

private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "NotificationChannelName"
        val descriptionText = "NotificationChannelDescriptionText"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


data class ReminderViewState(
    val textFromSpeech: String? = null,
)
