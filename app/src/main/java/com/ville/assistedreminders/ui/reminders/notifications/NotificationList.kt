package com.ville.assistedreminders.ui.reminders.notifications

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ville.assistedreminders.data.entity.Notification
import com.ville.assistedreminders.data.entity.Reminder
import com.ville.assistedreminders.data.entity.room.NotificationToReminder
import com.ville.assistedreminders.ui.reminders.notifications.notificationDialog.DeleteNotificationDialog
import com.ville.assistedreminders.util.viewModelProviderFactoryOf
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun NotificationList(
    reminder: Reminder,
    navController: NavController
) {
    val viewModel: NotificationListViewModel = viewModel(
        factory = viewModelProviderFactoryOf { NotificationListViewModel(reminder = reminder) }
    )
    val viewState by viewModel.state.collectAsState()

    val prevReminderId: MutableState<Long> = remember { mutableStateOf(0) }
    if (reminder.reminderId != prevReminderId.value) {
        viewModel.updateNotificationList(reminder)
    }
    prevReminderId.value = reminder.reminderId

    Column {
        NotificationColumn(
            list = viewState.notificationsForReminder,
            viewModel = viewModel,
            navController = navController
        )
    }
}

@Composable
private fun NotificationColumn(
    list: MutableLiveData<List<NotificationToReminder>>,
    viewModel: NotificationListViewModel,
    navController: NavController
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(list.value.orEmpty()) { item ->
            NotificationColumnItem(
                notification = item.notification,
                modifier = Modifier.fillParentMaxWidth(),
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

@Composable
private fun NotificationColumnItem(
    notification: Notification,
    modifier: Modifier = Modifier,
    viewModel: NotificationListViewModel,
    navController: NavController
) {
    ConstraintLayout(modifier = modifier) {
        val (notificationTime, iconRow, divider) = createRefs()
        val coroutineScope = rememberCoroutineScope()
        val openDeleteNotificationDialog = remember { mutableStateOf(false) }

        // Time
        Text(
            text = notification.notificationTime.formatToString(),
            fontStyle = FontStyle.Italic,
            fontSize = 13.sp,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(notificationTime) {
                    linkTo(
                        start = parent.start,
                        end = iconRow.end
                    )
                    top.linkTo(parent.top, 35.dp)
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
            if (notification.notificationLatitude != 0.0) {
                IconButton(
                    onClick = { navController.navigate(route = "map") },
                    modifier = Modifier
                        .size(40.dp)
                        .padding(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location"
                    )
                }
            }

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        openDeleteNotificationDialog.value = true
                    }
                },
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
            }
            DeleteNotificationDialog(openDeleteNotificationDialog, viewModel, notification)
        }

        Divider(
            Modifier.constrainAs(divider) {
                bottom.linkTo(parent.bottom)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )
    }
}


private fun Date.formatToString(): String {
    return SimpleDateFormat("kk:mm - E dd MMMM yyyy", Locale.getDefault()).format(this)
}

