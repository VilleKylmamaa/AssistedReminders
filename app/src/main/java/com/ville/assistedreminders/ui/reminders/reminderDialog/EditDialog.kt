package com.ville.assistedreminders.ui.reminders.reminderDialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.reminders.ReminderListViewModel
import com.ville.assistedreminders.ui.reminders.addReminder.formatDateToString
import com.ville.assistedreminders.ui.reminders.addReminder.formatTimeToString
import com.ville.assistedreminders.ui.theme.reminderIcon
import com.ville.assistedreminders.ui.theme.secondaryButtonBackground
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun EditDialog(
    openEditForm: MutableState<Boolean>,
    viewModel: ReminderListViewModel,
    reminder: Reminder,
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>,
    showAll: MutableState<Boolean>,
    showAllText: MutableState<String>
) {
    val coroutineScope = rememberCoroutineScope()
    val messageUpdate = remember { mutableStateOf(reminder.message) }
    val previousSpeechText = remember { mutableStateOf("") }
    val context = LocalContext.current
    val shownDate = remember { mutableStateOf(reminder.reminder_time.formatDateToString()) }
    val shownTime = remember { mutableStateOf(reminder.reminder_time.formatTimeToString()) }

    val scheduling: Calendar = Calendar.getInstance()
    scheduling.time = reminder.reminder_time

    MaterialTheme {
        Column {
            if (openEditForm.value) {
                messageUpdate.value = reminder.message
                if (speechText.value != previousSpeechText.value) {
                    messageUpdate.value = speechText.value.replaceFirstChar { it.uppercase() }
                }
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog
                        // or on the back button
                        speechText.value = ""
                        previousSpeechText.value = ""
                        messageUpdate.value = reminder.message
                        openEditForm.value = false
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
                                text = "Edit Reminder"
                            )
                        }
                    },
                    text = {
                        Column {
                            TextField(
                                label = { Text("Message") },
                                value = messageUpdate.value,
                                onValueChange = { messageUpdate.value = it },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            previousSpeechText.value = speechText.value
                                            speechToText(resultLauncher)
                                        },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .padding(horizontal = 5.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Mic,
                                            contentDescription = "Chosen icon",
                                            tint = MaterialTheme.colors.reminderIcon
                                        )
                                    }
                                }
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
                            ScheduleNotificationField(context, scheduling, shownDate, shownTime)
                            /*
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = { /* TODO */ },
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .size(55.dp)
                                    .padding(horizontal = 16.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(
                                    text = "Set Location",
                                    color = MaterialTheme.colors.onPrimary
                                )
                            }*/

                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Button(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(90.dp),
                                    onClick = {
                                        speechText.value = ""
                                        previousSpeechText.value = ""
                                        messageUpdate.value = reminder.message
                                        openEditForm.value = false
                                    }
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(90.dp),
                                    onClick = {
                                        if (isValid(messageUpdate.value, context)) {
                                            showAll.value = false
                                            showAllText.value = "Show All"
                                            coroutineScope.launch {
                                                reminder.message = messageUpdate.value
                                                reminder.reminder_time = scheduling.time
                                                viewModel.updateReminder(reminder)
                                            }
                                            openEditForm.value = false
                                        }
                                    }
                                ) {
                                    Text("Save")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

fun speechToText(resultLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk to input reminder message")
    resultLauncher.launch(intent)
}

private fun isValid(message: String, context: Context): Boolean {
    if (message.isEmpty()) {
        Toast.makeText(context, "Enter message for the reminder", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}


@Composable
private fun ScheduleNotificationField(
    context: Context,
    scheduling: Calendar,
    shownDate: MutableState<String>,
    shownTime: MutableState<String>,
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shownDate.value,
            fontSize = 13.sp,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        Button(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        scheduling[Calendar.DAY_OF_MONTH] = dayOfMonth
                        scheduling[Calendar.MONTH] = month
                        scheduling[Calendar.YEAR] = year
                        shownDate.value = scheduling.time.formatDateToString()
                    },
                    scheduling[Calendar.YEAR],
                    scheduling[Calendar.MONTH],
                    scheduling[Calendar.DAY_OF_MONTH]
                ).show()
            },
            enabled = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .width(125.dp)
                .size(40.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryButtonBackground,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Set Date",
                fontSize = 12.sp,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shownTime.value,
            fontSize = 13.sp,
            color = MaterialTheme.colors.onSecondary,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        Button(
            onClick = {
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        scheduling[Calendar.HOUR_OF_DAY] = hourOfDay
                        scheduling[Calendar.MINUTE] = minute
                        shownTime.value = scheduling.time.formatTimeToString()
                    },
                    scheduling[Calendar.HOUR_OF_DAY],
                    scheduling[Calendar.MINUTE],
                    true
                ).show()
            },
            enabled = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .width(125.dp)
                .size(40.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryButtonBackground,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Set Time",
                fontSize = 12.sp,
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}

