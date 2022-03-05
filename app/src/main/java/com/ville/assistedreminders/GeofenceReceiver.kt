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
        Log.d("meme1", "looking for geofence")
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

            // Test that the reported transition was of interest.
            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

                val triggeringGeoFences = geofencingEvent.triggeringGeofences
                val message = intent.getStringExtra("message")
                val reminderTime = intent.getLongExtra("reminderTime", 0)
                Log.d("meme2", "TRIGGERED: $message")

                // Notify if the time set for the reminder has been passed
                if (System.currentTimeMillis() > reminderTime) {
                    Log.d("meme3", "NOTIFY")
                    createGeoFenceNotification(context.applicationContext,"$message")
                    MainActivity.removeGeoFences(context, triggeringGeoFences)
                }
            }
        }
    }

}