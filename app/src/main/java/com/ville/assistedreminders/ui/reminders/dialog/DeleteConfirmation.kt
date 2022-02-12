package com.ville.assistedreminders.ui.reminders.dialog

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
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.reminders.ReminderListViewModel
import kotlinx.coroutines.launch


@Composable
fun DeleteConfirmation(
    openDeleteConfirmation: MutableState<Boolean>,
    viewModel: ReminderListViewModel,
    reminder: Reminder
) {
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Column {
            if (openDeleteConfirmation.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog
                        // or on the back button
                        openDeleteConfirmation.value = false
                    },
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .systemBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Delete reminder?")
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
                                    onClick = { openDeleteConfirmation.value = false }
                                ) {
                                    Text("Cancel")
                                }
                                Button(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(90.dp),
                                    onClick = {
                                        coroutineScope.launch {
                                            viewModel.deleteReminder(reminder)
                                        }
                                        openDeleteConfirmation.value = false
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