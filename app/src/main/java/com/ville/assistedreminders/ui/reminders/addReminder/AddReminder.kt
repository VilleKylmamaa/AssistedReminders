package com.ville.assistedreminders.ui.reminders.addReminder

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.theme.reminderIcon
import java.util.*


@Composable
fun AddReminder(
    viewModel: ReminderViewModel = viewModel(),
    onBackPress: () -> Unit,
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>
) {
    val coroutineScope = rememberCoroutineScope()
    val message = remember { mutableStateOf("") }
    val previousSpeechText = remember { mutableStateOf("") }
    val context = LocalContext.current

    if (speechText.value != previousSpeechText.value) {
        message.value = speechText.value.replaceFirstChar { it.uppercase() }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Row {
            TopAppBar {
                IconButton(
                    onClick = {
                        speechText.value = ""
                        onBackPress()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back button",
                        tint = MaterialTheme.colors.onSecondary
                    )
                }
                Text(
                    text = "Back",
                    color = MaterialTheme.colors.onSecondary
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Add New Reminder",
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Row {
                OutlinedTextField(
                    value = message.value,
                    onValueChange = { message.value = it },
                    label = { Text("Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
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

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (isValid(message.value, context)) {
                        coroutineScope.launch {
                            val loggedInAccount = viewModel.getLoggedInAccount()
                            if (loggedInAccount != null) {
                                viewModel.saveReminder(
                                    Reminder(
                                        message = message.value,
                                        location_x = "65.021545",
                                        location_y = "25.469885",
                                        reminder_time = Calendar.getInstance().time,
                                        creation_time = Calendar.getInstance().time,
                                        creator_id = loggedInAccount.accountId,
                                        reminder_seen = false,
                                        icon = "Circle"
                                    )
                                )
                                message.value = ""
                                speechText.value = ""
                            }
                        }
                        onBackPress()
                    }
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(55.dp)
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Save Reminder",
                    color = MaterialTheme.colors.onPrimary
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







