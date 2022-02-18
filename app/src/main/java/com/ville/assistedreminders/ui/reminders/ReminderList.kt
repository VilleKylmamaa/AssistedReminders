package com.ville.assistedreminders.ui.reminders

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.room.ReminderToAccount
import com.ville.assistedreminders.ui.reminders.dialog.ChooseIconForm
import com.ville.assistedreminders.ui.reminders.dialog.DeleteConfirmation
import com.ville.assistedreminders.ui.reminders.dialog.EditForm
import com.ville.assistedreminders.ui.theme.reminderIcon
import com.ville.assistedreminders.ui.theme.reminderMessage
import com.ville.assistedreminders.util.viewModelProviderFactoryOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ReminderList(
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>
) {
    val viewModel: ReminderListViewModel = viewModel(
        factory = viewModelProviderFactoryOf { ReminderListViewModel() }
    )
    val viewState by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val showAll = remember { mutableStateOf(false) }

    Column {
        ReminderColumn(
            list = viewState.remindersForAccount,
            viewModel = viewModel,
            resultLauncher = resultLauncher,
            speechText = speechText
        )
    }

    Spacer(modifier = Modifier.height(24.dp))
    // Button to show all reminders
    Button(
        onClick = {
            showAll.value = !showAll.value
            coroutineScope.launch {
                viewModel.showAllSwitch(showAll.value)
            }
        },
        enabled = true,
        modifier = Modifier
            .fillMaxWidth()
            .size(55.dp)
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        if (!showAll.value) {
            Text(
                text = "Show All",
                color = MaterialTheme.colors.onPrimary
            )
        } else {
            Text(
                text = "Don't Show All",
                color = MaterialTheme.colors.onPrimary
            )
        }
    }
}


@Composable
private fun ReminderColumn(
    list: MutableLiveData<List<ReminderToAccount>>,
    viewModel: ReminderListViewModel,
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(list.value.orEmpty()) { item ->
            ReminderColumnItem(
                reminder = item.reminder,
                modifier = Modifier.fillParentMaxWidth(),
                viewModel = viewModel,
                resultLauncher = resultLauncher,
                speechText = speechText
            )
        }
    }
}

@Composable
private fun ReminderColumnItem(
    reminder: Reminder,
    modifier: Modifier = Modifier,
    viewModel: ReminderListViewModel,
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>
) {
    val openChooseIcon = remember { mutableStateOf(false) }
    val openDeleteConfirmation = remember { mutableStateOf(false) }
    val openEditForm = remember { mutableStateOf(false) }
    speechText.value = ""

    ConstraintLayout(modifier = modifier) {
        val (chosenIcon, reminderMessage, reminderTime, iconRow, divider) = createRefs()

        var reminderIcon: ImageVector = Icons.Filled.Circle
        when (reminder.icon) {
            "Grade" -> { reminderIcon = Icons.Filled.Grade }
            "ReportProblem" -> { reminderIcon = Icons.Filled.ReportProblem }
            "Favorite" -> { reminderIcon = Icons.Filled.Favorite }
            "SelfImprovement" -> { reminderIcon = Icons.Filled.SelfImprovement }
            "SportsSoccer" -> { reminderIcon = Icons.Filled.SportsSoccer }
            "DirectionsRun" -> { reminderIcon = Icons.Filled.DirectionsRun }
            "SportsEsports" -> { reminderIcon = Icons.Filled.SportsEsports }
            "Pets" -> { reminderIcon = Icons.Filled.Pets }
            "ShoppingCart" -> { reminderIcon = Icons.Filled.ShoppingCart }
        }

        // Chosen icon
        IconButton(
            onClick = {
                openChooseIcon.value = true
            },
            modifier = Modifier
                .size(40.dp)
                .padding(5.dp)
                .constrainAs(chosenIcon) {
                    linkTo(
                        start = parent.start,
                        end = reminderMessage.end,
                        startMargin = 0.dp,
                        endMargin = 170.dp
                    )
                    top.linkTo(parent.top, 8.dp)
                }
        ) {
            Icon(
                imageVector = reminderIcon,
                contentDescription = "Chosen icon",
                tint = MaterialTheme.colors.reminderIcon
            )
        }

        // Message
        Text(
            text = reminder.message,
            color = MaterialTheme.colors.reminderMessage,
            fontSize = 18.sp,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(reminderMessage) {
                    linkTo(
                        start = chosenIcon.start,
                        end = iconRow.start,
                        startMargin = 45.dp,
                        endMargin = 16.dp
                    )
                    top.linkTo(parent.top, margin = 14.dp)
                    width = Dimension.preferredWrapContent
                }
        )

        // Time
        Text(
            text = reminder.reminder_time.formatToString(),
            fontStyle = FontStyle.Italic,
            fontSize = 12.sp,
            maxLines = 1,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(reminderTime) {
                    linkTo(
                        start = parent.start,
                        end = iconRow.start,
                        startMargin = 24.dp,
                        endMargin = 8.dp,
                        bias = 0f // float this towards the start
                    )
                    top.linkTo(reminderMessage.bottom, margin = 6.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    width = Dimension.preferredWrapContent
                }
        )

        // IconRow
        Row(
            modifier = Modifier
                .constrainAs(iconRow) {
                    top.linkTo(parent.top, 20.dp)
                    bottom.linkTo(parent.bottom, 20.dp)
                    end.linkTo(parent.end)
                }
        ) {
            IconButton(
                onClick = {
                    openEditForm.value = true
                },
                modifier = Modifier
                    .size(55.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit"
                )
            }

            IconButton(
                onClick = {
                    openDeleteConfirmation.value = true
                },
                modifier = Modifier
                    .size(55.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
            }
        }

        Divider(
            Modifier.constrainAs(divider) {
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )
    }

    // Dialogs that open from clicking the different icons
    ChooseIconForm(openChooseIcon, viewModel, reminder)
    DeleteConfirmation(openDeleteConfirmation, viewModel, reminder)
    EditForm(openEditForm, viewModel, reminder, resultLauncher, speechText)
}


private fun Date.formatToString(): String {
    return SimpleDateFormat("kk:mm - E dd MMMM yyyy", Locale.getDefault()).format(this)
}