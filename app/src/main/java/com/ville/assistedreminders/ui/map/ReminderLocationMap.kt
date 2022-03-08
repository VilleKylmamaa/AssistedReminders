package com.ville.assistedreminders.ui.map

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.ville.assistedreminders.GeofenceReceiver
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.Graph.accountRepository
import com.ville.assistedreminders.Graph.reminderRepository
import com.ville.assistedreminders.ui.MainActivity
import com.ville.assistedreminders.util.makeLongToast
import com.ville.assistedreminders.util.rememberMapViewWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

var geofenceId = 0

@Composable
fun ReminderLocationMap(
    navController: NavController,
    mainActivity: MainActivity,
    fusedLocationClient: FusedLocationProviderClient,
    geofencingClient: GeofencingClient
) {
    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(bottom = 36.dp)
    ) {
        AndroidView({mapView}) { mapView ->
            coroutineScope.launch {
                val map = mapView.awaitMap()
                //val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
                map.uiSettings.isZoomControlsEnabled = true

                if (!isLocationPermissionGranted()) {
                    val permissions = mutableListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                    ActivityCompat.requestPermissions(
                        mainActivity,
                        permissions.toTypedArray(),
                        LOCATION_REQUEST_CODE
                    )
                } else {

                    if (ActivityCompat.checkSelfPermission(
                            mainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            mainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode,
                        //      String[] permissions, int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        makeLongToast(Graph.appContext, "Missing location permissions")
                    }
                    map.isMyLocationEnabled = true

                    // Zoom to last known location
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            with(map) {
                                val latLng = LatLng(it.latitude, it.longitude)
                                moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL))
                            }
                        } else {
                            with(map) {
                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(65.01355297927051, 25.464019811372978),
                                        CAMERA_ZOOM_LEVEL
                                    )
                                )
                            }
                        }
                    }
                }

                setMapShortClick(map, coroutineScope)
                setMapLongClick(map, navController, mainActivity, geofencingClient)
            }
        }
    }
}

private fun setMapShortClick(
    map: GoogleMap,
    coroutineScope: CoroutineScope
) {
    map.setOnMapClickListener { clickedLocation ->
        map.clear()
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedLocation, CAMERA_ZOOM_LEVEL))

        coroutineScope.launch {
            val loggedInAccount = accountRepository.getLoggedInAccount()
            if (loggedInAccount != null) {
                reminderRepository.getRemindersForAccount(loggedInAccount.accountId)
                    .collect { reminderList ->
                        for (item in reminderList) {
                            val reminder = item.reminder
                            val reminderLatitude = reminder.location_x
                            val reminderLongitude = reminder.location_y
                            if (reminderLatitude != 0.0) {
                                val results = FloatArray(1)
                                Location.distanceBetween(
                                    clickedLocation.latitude, clickedLocation.longitude,
                                    reminderLatitude, reminderLongitude,
                                    results
                                )
                                val distance = results[0]

                                // Show reminders within 1km radius
                                if (distance < 1000){
                                    val snippet = String.format(
                                        Locale.getDefault(),
                                        "Lat: %1$.2f, Lng: %2$.2f",
                                        reminderLatitude,
                                        reminderLongitude
                                    )

                                    val reminderLatLng = LatLng(reminderLatitude, reminderLongitude)

                                    map.addMarker(
                                        MarkerOptions()
                                            .position(reminderLatLng)
                                            .title(reminder.message)
                                            .snippet(snippet)
                                    )

                                    map.addCircle(
                                        CircleOptions()
                                            .center(reminderLatLng)
                                            .radius(GEOFENCE_RADIUS.toDouble())
                                            .strokeColor(Color(0xA65A5A5A).toArgb())
                                            .fillColor(Color(0x59FF9BE6).toArgb())
                                    )
                                }
                            }
                        }
                    }
            }
        }

    }
}

private fun setMapLongClick(
    map: GoogleMap,
    navController: NavController,
    mainActivity: MainActivity,
    geofencingClient: GeofencingClient
) {
    map.setOnMapLongClickListener { clickedLocation ->
        map.clear()
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(clickedLocation, CAMERA_ZOOM_LEVEL))

        val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.2f, Lng: %2$.2f",
            clickedLocation.latitude,
            clickedLocation.longitude
        )

        map.addMarker(
            MarkerOptions()
                .position(clickedLocation)
                .title("Set reminder here")
                .snippet(snippet)
        )?.showInfoWindow().apply {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("location_data", clickedLocation)
        }

        map.addCircle(
            CircleOptions()
                .center(clickedLocation)
                .radius(GEOFENCE_RADIUS.toDouble())
                .strokeColor(Color(0xA65A5A5A).toArgb())
                .fillColor(Color(0x6680FFE6).toArgb())
        )

        createGeoFence(clickedLocation, mainActivity, geofencingClient)
    }
}


private fun createGeoFence(
    location: LatLng,
    mainActivity: MainActivity,
    geofencingClient: GeofencingClient
) {
    geofenceId += 1

    val geofence = Geofence.Builder()
        .setRequestId(geofenceId.toString())
        .setCircularRegion(location.latitude, location.longitude, GEOFENCE_RADIUS.toFloat())
        .setExpirationDuration(Geofence.NEVER_EXPIRE)
        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
        .setLoiteringDelay(GEOFENCE_DWELL_DELAY)
        .build()

    val geofenceRequest = GeofencingRequest.Builder()
        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
        .addGeofence(geofence)
        .build()

    val intent = Intent(mainActivity, GeofenceReceiver::class.java)
        .putExtra("reminderId", geofenceId)
        .putExtra("reminderTime", 0.toLong())
        .putExtra("message", "Latitude: ${location.latitude}" +
                    "\nLongitude: ${location.longitude}")

    val pendingIntent = PendingIntent.getBroadcast(
        mainActivity,
        0,
        intent,
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
            geofencingClient.addGeofences(geofenceRequest, pendingIntent)
                .addOnSuccessListener { Log.d("GeofenceAdded", "onSuccess: Geofence " +
                        "with id $geofenceId added") }
                .addOnFailureListener { Log.d("GeofenceNotAdded", "onFailure: " +
                        "Geofence NOT added") }
        }
    } else {
        geofencingClient.addGeofences(geofenceRequest, pendingIntent)
            .addOnSuccessListener { Log.d("GeofenceAdded", "onSuccess: Geofence " +
                    "with id $geofenceId added") }
            .addOnFailureListener { Log.d("GeofenceNotAdded", "onFailure: " +
                    "Geofence NOT added") }
    }
}

private fun isLocationPermissionGranted() : Boolean {
    return ContextCompat.checkSelfPermission(
        Graph.appContext, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        Graph.appContext, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}