package com.ville.assistedreminders.util

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ville.assistedreminders.Graph
import java.lang.Exception

class NotificationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {

    override fun doWork(): Result {
        return try {
            var message = inputData.getString("NOTIFICATION_MESSAGE") ?: return Result.failure()
            val location = inputData.getString("NOTIFICATION_LOCATION")
            val notificationId = inputData.getLong("NOTIFICATION_ID", 0)

            if (location == null) {
                notifyReminder(message, notificationId)
            } else {
                var title = inputData.getString("NOTIFICATION_TITLE") ?: return Result.failure()
                notifyGeofence(title, message, notificationId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}