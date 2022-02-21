package com.ville.assistedreminders.ui.reminders.notifications.notificationDialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.ui.reminders.notifications.NotificationListViewModel
import kotlinx.coroutines.launch


@Composable
fun DeleteNotificationDialog(
    openDeleteNotificationDialog: MutableState<Boolean>,
    viewModel: NotificationListViewModel,
    notification: Notification
) {
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Column {
            if (openDeleteNotificationDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog
                        // or on the back button
                        openDeleteNotificationDialog.value = false
                    },
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .systemBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Delete notification?")
                        }
                    },
                    buttons = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .systemBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Button(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(90.dp),
                                    onClick = { openDeleteNotificationDialog.value = false }
                                ) {
                                    Text("Cancel")
                                }
                                Button(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(90.dp),
                                    onClick = {
                                        coroutineScope.launch {
                                            viewModel.deleteNotification(notification)
                                        }
                                        openDeleteNotificationDialog.value = false
                                    }
                                ) {
                                    Text("Yes")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}