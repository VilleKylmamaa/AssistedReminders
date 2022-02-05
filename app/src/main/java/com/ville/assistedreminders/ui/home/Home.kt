package com.ville.assistedreminders.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ville.assistedreminders.ui.reminders.ReminderList
import com.ville.assistedreminders.ui.theme.ThemeState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun Home(
    viewModel: HomeViewModel = viewModel(),
    navController: NavController
) {
    // Stops back button from going back to login screen
    BackHandler(true) { /* Do nothing */ }

    Surface(modifier = Modifier.fillMaxSize()) {
        HomeContent(
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel
) {
    Scaffold(
        modifier = Modifier.padding(bottom = 24.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(route = "reminder") },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = Color.Black,
                modifier = Modifier.padding(all = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) {
        Column (
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
        ) {
            HomeAppBar(navController, viewModel)
            ReminderList()
        }
    }
}

@Composable
fun HomeAppBar(
    navController: NavController,
    viewModel: HomeViewModel,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    val checkedStateTheme = remember { mutableStateOf(true) }
    var currentUsername by remember { mutableStateOf("") }

    LaunchedEffect(scaffoldState.snackbarHostState) {
        currentUsername = viewModel.getLoggedInAccountUsername()
    }

    TopAppBar(
        title = {
            Text(
                text = "Reminders",
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
            )
        },
        backgroundColor = MaterialTheme.colors.primary,
        actions = {

            Button(
                onClick = {
                    expanded = !expanded
                    coroutineScope.launch {
                        currentUsername = viewModel.getLoggedInAccountUsername()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = currentUsername,
                    fontSize = 12.sp
                )
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = stringResource(R.string.account),
                    modifier = Modifier.size(40.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(MaterialTheme.colors.primaryVariant)
                    .padding(horizontal = 5.dp)
            ) {
                Button(
                    onClick = {
                        expanded = false
                        navController.navigate("username")
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondaryVariant,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(165.dp)
                ) {
                    Text(text = "Change username")
                }

                Spacer(modifier = Modifier.height(3.dp))
                Button(
                    onClick = {
                        expanded = false
                        navController.navigate("password")
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondaryVariant,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(165.dp)
                ) {
                    Text(text = "Change password")
                }

                Spacer(modifier = Modifier.height(3.dp))
                Button(
                    onClick = {
                        ThemeState.darkTheme = !ThemeState.darkTheme
                        checkedStateTheme.value = !checkedStateTheme.value
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondaryVariant,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(165.dp)
                ) {
                    Switch(
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF1A41CF),
                            uncheckedThumbColor = Color(0xFF1A89FF),
                        ),
                        checked = ThemeState.darkTheme,
                        onCheckedChange = {
                            checkedStateTheme.value = it
                            ThemeState.darkTheme = !ThemeState.darkTheme
                        }
                    )
                    Text(text = "Dark theme")
                }

                Spacer(modifier = Modifier.height(3.dp))
                Button(
                    onClick = {
                        expanded = false
                        navController.navigate("login")
                        coroutineScope.launch {
                            val loggingOutAccount = viewModel.getLoggedInAccount()
                            if (loggingOutAccount != null) {
                                loggingOutAccount.isLoggedIn = false
                                    viewModel.updateAccount(loggingOutAccount)
                                }
                        }
                    },
                    enabled = true,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.secondaryVariant,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(165.dp)
                ) {
                    Text(text = "Logout")
                }
            }
        }
    )
}




