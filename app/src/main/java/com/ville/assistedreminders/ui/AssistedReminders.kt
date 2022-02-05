package com.ville.assistedreminders.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ville.assistedreminders.ui.accountSettings.ChangePassword
import com.ville.assistedreminders.ui.accountSettings.ChangeUsername
import com.ville.assistedreminders.ui.home.Home
import com.ville.assistedreminders.ui.login.Login
import com.ville.assistedreminders.ui.login.Signup
import com.ville.assistedreminders.ui.reminders.addReminder.Reminder

@Composable
fun AssistedReminders(
    appState: AssistedRemindersAppState = rememberAssistedRemindersAppState()
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
        composable(route = "home") {
            Home(navController = appState.navController)
        }
        composable(route = "reminder") {
            Reminder(onBackPress = appState::navigateBack)
        }
        composable(route = "username") {
            ChangeUsername(onBackPress = appState::navigateBack)
        }
        composable(route = "password") {
            ChangePassword(onBackPress = appState::navigateBack)
        }
    }
}

