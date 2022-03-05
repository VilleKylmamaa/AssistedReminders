package com.ville.assistedreminders.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import com.ville.assistedreminders.Graph
import com.ville.assistedreminders.ui.MainActivity
import com.ville.assistedreminders.util.makeLongToast
import com.ville.assistedreminders.util.rememberMapViewWithLifecycle
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun ReminderLocationMap(
    navController: NavController,
    mainActivity: MainActivity
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
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(mainActivity)
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

                setMapLongClick(
                    map = map,
                    navController = navController
                )
            }
        }
    }
}

private fun setMapLongClick(
    map: GoogleMap,
    navController: NavController
) {
    map.setOnMapLongClickListener { latitudeLongitude ->
        map.clear()

        val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.2f, Lng: %2$.2f",
            latitudeLongitude.latitude,
            latitudeLongitude.longitude
        )

        map.addMarker(
            MarkerOptions()
                .position(latitudeLongitude)
                .title("Reminder location")
                .snippet(snippet)
        ).apply {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("location_data", latitudeLongitude)
        }

        map.addCircle(
            CircleOptions()
                .center(latitudeLongitude)
                .radius(GEOFENCE_RADIUS.toDouble())
                .strokeColor(Color(0xA65A5A5A).toArgb())
                .fillColor(Color(0x6680FFE6).toArgb())
        )
    }
}


private fun isLocationPermissionGranted() : Boolean {
    return ContextCompat.checkSelfPermission(
        Graph.appContext, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        Graph.appContext, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}