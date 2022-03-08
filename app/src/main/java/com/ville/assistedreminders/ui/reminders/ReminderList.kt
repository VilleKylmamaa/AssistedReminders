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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.room.ReminderToAccount
import com.ville.assistedreminders.ui.reminders.reminderDialog.AddNotificationDialog
import com.ville.assistedreminders.ui.reminders.reminderDialog.ChooseIconDialog
import com.ville.assistedreminders.ui.reminders.reminderDialog.DeleteDialog
import com.ville.assistedreminders.ui.reminders.reminderDialog.EditDialog
import com.ville.assistedreminders.ui.theme.*
import com.ville.assistedreminders.util.viewModelProviderFactoryOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun ReminderList(
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>,
    navController: NavController
) {
    val viewModel: ReminderListViewModel = viewModel(
        factory = viewModelProviderFactoryOf { ReminderListViewModel() }
    )
    val viewState by viewModel.state.collectAsState()
    val showAll = remember { mutableStateOf(false) }
    val showAllText = remember { mutableStateOf("Show All") }

    Column {
        ReminderColumn(
            list = viewState.remindersForAccount,
            viewModel = viewModel,
            resultLauncher = resultLauncher,
            speechText = speechText,
            showAll = showAll,
            showAllText = showAllText,
            navController = navController
        )
    }

    Spacer(modifier = Modifier.height(24.dp))
    Row {
        ShowAllButton(viewModel, showAll, showAllText)
        OpenMapButton(navController)
    }
}

@Composable
private fun ReminderColumn(
    list: MutableLiveData<List<ReminderToAccount>>,
    viewModel: ReminderListViewModel,
    resultLauncher: ActivityResultLauncher<Intent>,
    speechText: MutableState<String>,
    showAll: MutableState<Boolean>,
    showAllText: MutableState<String>,
    navController: NavController
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
                speechText = speechText,
                showAll = showAll,
                showAllText = showAllText,
                navController = navController
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
    speechText: MutableState<String>,
    showAll: MutableState<Boolean>,
    showAllText: MutableState<String>,
    navController: NavController
) {
    val openChooseIconDialog = remember { mutableStateOf(false) }
    val openDeleteDialog = remember { mutableStateOf(false) }
    val openEditDialog = remember { mutableStateOf(false) }
    val openNotificationDialog = remember { mutableStateOf(false) }
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
                openChooseIconDialog.value = true
            },
            modifier = Modifier
                .size(40.dp)
                .padding(5.dp)
                .constrainAs(chosenIcon) {
                    linkTo(
                        start = parent.start,
                        end = reminderMessage.end,
                        startMargin = 0.dp,
                        endMargin = 125.dp
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
                onClick = { openNotificationDialog.value = true },
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Add Notification"
                )
            }
            IconButton(
                onClick = { openEditDialog.value = true },
                modifier = Modifier
                    .size(50.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit"
                )
            }

            IconButton(
                onClick = { openDeleteDialog.value = true },
                modifier = Modifier
                    .size(50.dp)
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
    ChooseIconDialog(openChooseIconDialog, viewModel, reminder, showAll, showAllText)
    DeleteDialog(openDeleteDialog, viewModel, reminder, showAll, showAllText)
    EditDialog(openEditDialog, viewModel, reminder, resultLauncher, speechText, showAll, showAllText)
    AddNotificationDialog(openNotificationDialog, viewModel, reminder, navController)
}

@Composable
private fun ShowAllButton(
    viewModel: ReminderListViewModel,
    showAll: MutableState<Boolean>,
    showAllText: MutableState<String>
) {
    val coroutineScope = rememberCoroutineScope()

    Spacer(modifier = Modifier.height(24.dp))
    // Button to show all reminders
    Button(
        onClick = {
            showAll.value = !showAll.value
            if (showAll.value) {
                showAllText.value = "Hide"
            } else {
                showAllText.value = "Show All"
            }
            coroutineScope.launch {
                viewModel.showAllSwitch(showAll.value)
            }
        },
        enabled = true,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.showAllButtonBackground,
            contentColor = Color.Black
        ),
        modifier = Modifier
            .width(130.dp)
            .size(45.dp)
            .padding(horizontal = 14.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = showAllText.value,
            color = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
private fun OpenMapButton(
    navController: NavController
) {
    Spacer(modifier = Modifier.height(24.dp))
    Button(
        onClick = { navController.navigate(route = "map") },
        enabled = true,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF9DFFB9),
            contentColor = Color.Black
        ),
        modifier = Modifier
            .width(140.dp)
            .size(45.dp)
            .padding(horizontal = 14.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = "Open Map",
            color = MaterialTheme.colors.onPrimary
        )
    }
}

fun Date.formatToString(): String {
    return SimpleDateFormat("kk:mm - E dd MMMM yyyy", Locale.getDefault()).format(this)
}

