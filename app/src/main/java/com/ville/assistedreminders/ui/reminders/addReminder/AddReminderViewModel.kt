package com.ville.assistedreminders.ui.reminders.addReminder

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import com.ville.assistedreminders.GeofenceReceiver
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.Graph.accountRepository
import com.ville.assistedreminders.Graph.notificationRepository
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.repository.ReminderRepository
import com.ville.assistedreminders.ui.MainActivity
import com.ville.assistedreminders.ui.map.*
import com.ville.assistedreminders.util.*
import java.util.*

class ReminderViewModel(
    private val reminderRepository: ReminderRepository = Graph.reminderRepository
): ViewModel() {
    suspend fun getLoggedInAccount(): Account? {
        return accountRepository.getLoggedInAccount()
    }

    suspend fun saveReminder(
        reminder: Reminder,
        scheduling: Calendar,
        location: LatLng?,
        scheduleNotification: Boolean,
        locationNotification: Boolean,
        mainActivity: MainActivity,
        geofencingClient: GeofencingClient
    ) {
        // Save new reminder and get its id
        val newReminderId = reminderRepository.addReminder(reminder)

        // Schedule a notification if notify checkbox was checked
        // and the given time hasn't already passed
        if (scheduleNotification
            && reminder.reminder_time.time > System.currentTimeMillis()
            && !locationNotification
        ) {
            val newNotificationID = notificationRepository.addNotification(
                Notification(
                    notificationTime = scheduling.time,
                    reminder_id = newReminderId
                )
            )
            scheduleReminderNotification(newNotificationID, reminder, scheduling)
        }

        // Set up a geofence if the location was given and the location
        // checkbox was checked
        if (locationNotification && location != null) {
            notificationRepository.addNotification(
                Notification(
                    notificationTime = scheduling.time,
                    notificationLatitude = location.latitude,
                    notificationLongitude = location.longitude,
                    reminder_id = newReminderId
                )
            )

            createGeoFence(
                newReminderId,
                location,
                reminder.reminder_time,
                reminder.message,
                mainActivity,
                geofencingClient
            )
        }
    }

    private fun createGeoFence(
        reminderId: Long,
        location: LatLng,
        reminderTime: Date,
        reminderMessage: String,
        mainActivity: MainActivity,
        geofencingClient: GeofencingClient
    ) {
        val geofence = Geofence.Builder()
            .setRequestId(reminderId.toString())
            .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .addGeofence(geofence)
            .build()

        val geofenceIntent = Intent(mainActivity, GeofenceReceiver::class.java)
            .putExtra("reminderId", reminderId)
            .putExtra("reminderTime", reminderTime.time)
            .putExtra(
                    "message", "Message: $reminderMessage" +
                    "\n\nLatitude: ${location.latitude}" +
                    "\nLongitude: ${location.longitude}"
            )

        val geofencePendingIntent = PendingIntent.getBroadcast(
            mainActivity,
            0,
            geofenceIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    Graph.appContext, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    mainActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    GEOFENCE_LOCATION_REQUEST_CODE
                )
            } else {
                geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent)
                    .addOnSuccessListener { Log.d("GeofenceAdded", "onSuccess: Geofence " +
                            "with id $reminderId added") }
                    .addOnFailureListener { Log.d("GeofenceNotAdded", "onFailure: " +
                            "Geofence NOT added") }
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, geofencePendingIntent)
                .addOnSuccessListener { Log.d("GeofenceAdded", "onSuccess: Geofence " +
                        "with id $reminderId added") }
                .addOnFailureListener { Log.d("GeofenceNotAdded", "onFailure: " +
                        "Geofence NOT added") }
        }
    }

    init {
        createNotificationChannel(context = Graph.appContext)
    }
}

/*
data class ReminderViewState(
    val textFromSpeech: String? = null,
)
*/