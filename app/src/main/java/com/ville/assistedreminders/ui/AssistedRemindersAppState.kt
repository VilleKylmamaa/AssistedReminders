package com.ville.assistedreminders.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class AssistedRemindersAppState(
    val navController: NavHostController
) {
    fun navigateBack() {
        navController.popBackStack()
    }
}

@Composable
fun rememberAssistedRemindersAppState(
    navController: NavHostController = rememberNavController()
) = remember(navController) {
    AssistedRemindersAppState(navController)
}