package com.ville.assistedreminders.ui.reminders.addReminder

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.systemBarsPadding
import kotlinx.coroutines.launch
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.theme.reminderIcon
import com.ville.assistedreminders.ui.theme.secondaryButtonBackground
import com.ville.assistedreminders.util.makeLongToast
import com.ville.assistedreminders.util.notifyNewReminder
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AddReminder(
    viewModel: ReminderViewModel = viewModel(),
    onBackPress: () -> Unit,
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val message = remember { mutableStateOf("") }
    val previousSpeechText = remember { mutableStateOf("") }
    val scheduling: Calendar = Calendar.getInstance()
    val shownDate = remember { mutableStateOf("Date not set") }
    val shownTime = remember { mutableStateOf("Time not set") }
    val notifyCheckedState = remember { mutableStateOf(true) }


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
                                contentDescription = "Mic icon",
                                tint = MaterialTheme.colors.reminderIcon
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            ScheduleNotificationField(context, scheduling, shownDate, shownTime, notifyCheckedState)
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
            }
            */
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (isValid(message.value, context)) {
                        coroutineScope.launch {
                            val loggedInAccount = viewModel.getLoggedInAccount()
                            if (loggedInAccount != null) {
                                val newReminder = Reminder(
                                    message = message.value,
                                    location_x = "65.021545",
                                    location_y = "25.469885",
                                    reminder_time = scheduling.time,
                                    creation_time = Calendar.getInstance().time,
                                    creator_id = loggedInAccount.accountId,
                                    reminder_seen = false,
                                    icon = "Circle"
                                )
                                viewModel.saveReminder(
                                    newReminder,
                                    scheduling,
                                    notifyCheckedState.value
                                )
                                notifyNewReminder(newReminder)
                                if (notifyCheckedState.value) {
                                    makeLongToast(context, "New reminder will notify you on" +
                                            " ${scheduling.time.formatTimeToString()}" +
                                            " ${scheduling.time.formatDateToString()}")
                                } else {
                                    makeLongToast(context,
                                        "New reminder without notification added")
                                }
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

@Composable
private fun ScheduleNotificationField(
    context: Context,
    scheduling: Calendar,
    shownDate: MutableState<String>,
    shownTime: MutableState<String>,
    notifyCheckedState: MutableState<Boolean>
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shownDate.value,
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
                .width(135.dp)
                .size(45.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryButtonBackground,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Set Date",
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
                .width(135.dp)
                .size(45.dp)
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryButtonBackground,
                contentColor = Color.Black
            )
        ) {
            Text(
                text = "Set Time",
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
        Text (
            text = "Notify me on the set date and time",
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        Checkbox(
            checked = notifyCheckedState.value,
            onCheckedChange = { notifyCheckedState.value = it },
            modifier = Modifier.padding(horizontal = 10.dp),
            colors = CheckboxDefaults.colors(MaterialTheme.colors.secondaryButtonBackground)
        )
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

fun Date.formatDateToString(): String {
    return SimpleDateFormat("E dd MMMM yyyy", Locale.getDefault()).format(this)
}

fun Date.formatTimeToString(): String {
    return SimpleDateFormat("kk:mm", Locale.getDefault()).format(this)
}



