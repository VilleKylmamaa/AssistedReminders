package com.ville.assistedreminders.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class NotificationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {

    override fun doWork(): Result {
        return try {
            val message = inputData.getString("NOTIFICATION_MESSAGE") ?: return Result.failure()
            val dueDate = inputData.getString("NOTIFICATION_DUE") ?: return Result.failure()
            val notificationId = inputData.getLong("NOTIFICATION_ID", 0)
            createReminderNotification(message, dueDate, notificationId)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}