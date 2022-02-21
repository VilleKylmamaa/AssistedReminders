package com.ville.assistedreminders.ui.reminders.reminderDialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.ui.reminders.ReminderListViewModel
import com.ville.assistedreminders.ui.reminders.addReminder.formatDateToString
import com.ville.assistedreminders.ui.reminders.addReminder.formatTimeToString
import com.ville.assistedreminders.ui.reminders.notifications.NotificationList
import com.ville.assistedreminders.ui.theme.secondaryButtonBackground
import com.ville.assistedreminders.util.makeLongToast
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun AddNotificationDialog(
    openNotificationDialog: MutableState<Boolean>,
    viewModel: ReminderListViewModel,
    reminder: Reminder
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val scheduling: Calendar = Calendar.getInstance()
    val shownDate = remember { mutableStateOf("Date not set") }
    val shownTime = remember { mutableStateOf("Time not set") }

    MaterialTheme {
        Column {
            if (openNotificationDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog
                        // or on the back button
                        openNotificationDialog.value = false
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
                                text = "Add Notification"
                            )
                        }
                    },
                    text = {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = shownDate.value,
                                    color = MaterialTheme.colors.onSecondary,
                                    modifier = Modifier.padding(horizontal = 0.dp)
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
                                        .width(115.dp)
                                        .size(40.dp)
                                        .padding(horizontal = 6.dp),
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
                                    modifier = Modifier.padding(horizontal = 0.dp)
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
                                        .width(115.dp)
                                        .size(40.dp)
                                        .padding(horizontal = 6.dp),
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .systemBarsPadding(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Scheduled Notifications"
                                )
                            }
                            NotificationList(reminder)
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
                                    onClick = {
                                        openNotificationDialog.value = false
                                    }
                                ) {
                                    Text("Close")
                                }

                                Button(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .width(90.dp),
                                    onClick = {
                                        if (isValid(scheduling, context)) {
                                            coroutineScope.launch {
                                                viewModel.addNotification(
                                                    Notification(
                                                      notificationTime = scheduling.time,
                                                      reminder_id = reminder.reminderId
                                                    ),
                                                    reminder,
                                                    scheduling
                                                )
                                            }
                                            makeLongToast(context,
                                                "New notification scheduled:" +
                                                " ${scheduling.time.formatTimeToString()}" +
                                                " ${scheduling.time.formatDateToString()}")
                                            openNotificationDialog.value = false
                                        }
                                    }
                                ) {
                                    Text("Add")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun isValid(scheduling: Calendar, context: Context): Boolean {
    if (scheduling.timeInMillis < System.currentTimeMillis()) {
        Toast.makeText(context, "Notification time has already passed", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}



