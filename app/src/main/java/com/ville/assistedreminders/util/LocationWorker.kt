package com.ville.assistedreminders.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.ville.assistedreminders.Graph
import java.lang.Exception

class LocationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {

    override fun doWork(): Result {
        return try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(Graph.appContext)
            if (ActivityCompat.checkSelfPermission(
                    Graph.appContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    Graph.appContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    Log.d("geofenceWorkerLocation", location.toString())
            }

            Result.success()
        } catch (e: Exception) {
            Log.d("meme", "feilaa")
            Result.failure()
        }
    }

}