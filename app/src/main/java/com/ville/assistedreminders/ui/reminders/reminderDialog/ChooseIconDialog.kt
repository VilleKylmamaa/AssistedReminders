package com.ville.assistedreminders.ui.reminders.reminderDialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.reminders.ReminderListViewModel
import com.ville.assistedreminders.ui.theme.reminderIcon
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun ChooseIconDialog(
    openChooseIcon: MutableState<Boolean>,
    viewModel: ReminderListViewModel,
    reminder: Reminder,
    showAll: MutableState<Boolean>,
    showAllText: MutableState<String>
) {
    val coroutineScope = rememberCoroutineScope()

    MaterialTheme {
        Column {
            if (openChooseIcon.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog
                        // or on the back button
                        openChooseIcon.value = false
                    },
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .systemBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Choose Icon"
                            )
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
                                IconButton(
                                    onClick = {
                                        reminder.icon = "Grade"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Grade,
                                        contentDescription = "Star",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        reminder.icon = "ReportProblem"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ReportProblem,
                                        contentDescription = "Warning triangle",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        reminder.icon = "Favorite"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Heart",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        reminder.icon = "SelfImprovement"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SelfImprovement,
                                        contentDescription = "Self improvement",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        reminder.icon = "DirectionsRun"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.DirectionsRun,
                                        contentDescription = "Running",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        reminder.icon = "SportsEsports"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.SportsEsports,
                                        contentDescription = "Gaming controller",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        reminder.icon = "Pets"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Pets,
                                        contentDescription = "Pet footprint",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        reminder.icon = "ShoppingCart"
                                        coroutineScope.launch { viewModel.updateReminder(reminder) }
                                        showAll.value = false
                                        showAllText.value = "Show All"
                                        openChooseIcon.value = false
                                    },
                                    modifier = Modifier
                                        .size(50.dp)
                                        .padding(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ShoppingCart,
                                        contentDescription = "Shopping cart",
                                        tint = MaterialTheme.colors.reminderIcon
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}