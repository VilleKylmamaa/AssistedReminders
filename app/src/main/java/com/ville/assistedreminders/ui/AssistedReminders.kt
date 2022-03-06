package com.ville.assistedreminders.ui

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.ville.assistedreminders.ui.accountSettings.ChangePassword
import com.ville.assistedreminders.ui.accountSettings.ChangeUsername
import com.ville.assistedreminders.ui.home.Home
import com.ville.assistedreminders.ui.login.Login
import com.ville.assistedreminders.ui.login.Signup
import com.ville.assistedreminders.ui.map.ReminderLocationMap
import com.ville.assistedreminders.ui.reminders.addReminder.AddReminder

@Composable
fun AssistedReminders(
    appState: AssistedRemindersAppState = rememberAssistedRemindersAppState(),
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>,
    mainActivity: MainActivity,
    //geofencingClient: GeofencingClient,
    //fusedLocationClient: FusedLocationProviderClient
){
    NavHost(
        navController = appState.navController,
        startDestination = "login"
    ) {
        composable(route = "login") {
            Login(navController = appState.navController)
        }
        composable(route = "signup") {
            Signup(navController = appState.navController, onBackPress = appState::navigateBack)
        }
        composable(route = "username") {
            ChangeUsername(onBackPress = appState::navigateBack)
        }
        composable(route = "password") {
            ChangePassword(onBackPress = appState::navigateBack)
        }

        composable(route = "home") {
            Home(navController = appState.navController,
                resultLauncher = resultLauncher,
                speechText = speechText
            )
        }

        composable(route = "addReminder") {
            AddReminder(
                navController = appState.navController,
                onBackPress = appState::navigateBack,
                resultLauncher = resultLauncher,
                speechText = speechText,
                mainActivity = mainActivity,
                //geofencingClient = geofencingClient
            )
        }

        composable(route = "map") {
            ReminderLocationMap(
                mainActivity = mainActivity,
                navController = appState.navController,
                //fusedLocationClient = fusedLocationClient,
                //geofencingClient = geofencingClient
            )
        }
    }
}

