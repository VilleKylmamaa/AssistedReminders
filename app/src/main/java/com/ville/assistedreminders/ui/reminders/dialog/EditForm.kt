package com.ville.assistedreminders.ui.reminders.dialog

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.reminders.ReminderListViewModel
import com.ville.assistedreminders.ui.theme.reminderIcon
import kotlinx.coroutines.launch

@Composable
fun EditForm(
    openEditForm: MutableState<Boolean>,
    viewModel: ReminderListViewModel,
    reminder: Reminder,
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>
) {
    val coroutineScope = rememberCoroutineScope()
    val messageUpdate = remember { mutableStateOf(reminder.message) }
    val previousSpeechText = remember { mutableStateOf("") }
    val context = LocalContext.current

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
                                    text = "Schedule",
                                    color = MaterialTheme.colors.onPrimary
                                )
                            }

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
                            }

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
                                            coroutineScope.launch {
                                                reminder.message = messageUpdate.value
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



