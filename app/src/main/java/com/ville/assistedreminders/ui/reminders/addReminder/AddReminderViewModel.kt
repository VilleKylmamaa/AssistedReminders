package com.ville.assistedreminders.ui.reminders.addReminder

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
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
        mainActivity: MainActivity
    ) {
        // Schedule a notification in the future if notify checkbox was checked
        // and the given time hasn't already passed
        val newReminderId = reminderRepository.addReminder(reminder)
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

        if (locationNotification && location != null) {
            createGeoFence(
                newReminderId,
                location,
                reminder.reminder_time,
                reminder.message,
                mainActivity
            )
        }
    }

    private fun createGeoFence(
        reminderId: Long,
        location: LatLng,
        reminderTime: Date,
        reminderMessage: String,
        mainActivity: MainActivity
    ) {
        val geofencingClient = LocationServices.getGeofencingClient(mainActivity)

        val geofence = Geofence.Builder()
            .setRequestId(GEOFENCE_ID)
            .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
            .setExpirationDuration(GEOFENCE_EXPIRATION.toLong())
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
            .build()

        val geofenceRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        val intent = Intent(Graph.appContext, GeofenceReceiver::class.java)
            .putExtra("reminderId", reminderId)
            .putExtra("reminderTime", reminderTime.time)
            .putExtra(
                    "message", "Message: $reminderMessage" +
                    "\n\nLatitude: ${location.latitude}" +
                    "\nLongitude: ${location.longitude}"
            )

        val pendingIntent = PendingIntent.getBroadcast(
            Graph.appContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
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
                geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            }
        } else {
            geofencingClient.addGeofences(geofenceRequest, pendingIntent)
        }
    }

    init {
        createNotificationChannel(context = Graph.appContext)
    }
}

data class ReminderViewState(
    val textFromSpeech: String? = null,
)
