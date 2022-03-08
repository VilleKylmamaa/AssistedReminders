package com.ville.assistedreminders

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import com.ville.assistedreminders.ui.MainActivity
import com.ville.assistedreminders.util.createGeoFenceNotification

class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("geofenceLooking", "Looking for geofences")
        if (context != null && intent != null) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            // Get the transition type.
            val geofenceTransition = geofencingEvent.geofenceTransition
            Log.d("geofenceTransition", "Transition type: $geofenceTransition")

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

                val triggeringGeoFences = geofencingEvent.triggeringGeofences
                val message = intent.getStringExtra("message")
                val reminderTime = intent.getLongExtra("reminderTime", 0)
                Log.d("geofenceTriggered", "Message: $message, time: $reminderTime")

                // Notify if the time set for the reminder has been passed
                if (System.currentTimeMillis() > reminderTime) {
                    Log.d("geofenceNotify", "Geofence notification sent")
                    createGeoFenceNotification(context.applicationContext, "$message")
                    MainActivity.removeGeoFences(context, triggeringGeoFences)
                }
            } else {
                Log.e("geofenceReceiver", "Invalid type transition $geofenceTransition")
            }
        }
    }

}