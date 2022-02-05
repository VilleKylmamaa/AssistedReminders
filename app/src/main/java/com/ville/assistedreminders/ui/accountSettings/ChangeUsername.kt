package com.ville.assistedreminders.ui.accountSettings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.Graph.accountRepository
import kotlinx.coroutines.launch

@Composable
fun ChangeUsername(
    onBackPress: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val newUsername = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Row {
            TopAppBar {
                IconButton(
                    onClick = onBackPress
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
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
                text = "Change Username",
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = newUsername.value,
                onValueChange = { data -> newUsername.value = data },
                label = { Text("New username") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (isValid(newUsername.value, context)) {
                        coroutineScope.launch {
                            val updateAccount = accountRepository.getLoggedInAccount()
                            if (updateAccount != null) {
                                val newUsernameString = newUsername.value
                                updateAccount.username = newUsernameString
                                accountRepository.updateAccount(updateAccount)
                                makeToast(context, "New username: $newUsernameString")
                            }
                        }
                        onBackPress()
                    } else {
                        makeToast(context, "Username has to be at least 5 characters long")
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
                    text = "Change your username",
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}


private fun makeToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

private fun isValid(username: String, context: Context): Boolean {
    if (username.filter { !it.isWhitespace() }.length < username.length) {
        makeToast(context, "Username can't have whitespace")
        return false
    }
    if (username.length < 5) {
        makeToast(context, "Username has to be at least 5 characters long")
        return false
    }
    return true
}