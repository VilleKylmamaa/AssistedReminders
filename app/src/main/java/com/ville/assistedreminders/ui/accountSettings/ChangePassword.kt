package com.ville.assistedreminders.ui.accountSettings


import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.Graph.accountRepository
import com.ville.assistedreminders.util.makeShortToast
import kotlinx.coroutines.launch

@Composable
fun ChangePassword(
    onBackPress: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val newPassword = rememberSaveable { mutableStateOf("") }
    val repeatPassword = rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize()) {
        Row {
            TopAppBar {
                IconButton(
                    onClick = onBackPress
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
                text = "Change Password",
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = newPassword.value,
                onValueChange = { data -> newPassword.value = data },
                label = { Text("New password") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(18.dp))
            OutlinedTextField(
                value = repeatPassword.value,
                onValueChange = { data -> repeatPassword.value = data },
                label = {Text("Repeat password")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (isValid(newPassword.value, repeatPassword.value, context)) {
                        coroutineScope.launch {
                            val updateAccount = accountRepository.getLoggedInAccount()
                            if (updateAccount != null) {
                                updateAccount.password = newPassword.value
                                accountRepository.updateAccount(updateAccount)
                                makeShortToast(context, "Password changed")
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
                    text = "Change your password",
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

private fun isValid(password: String, repeatPassword: String, context: Context): Boolean {
    if (password.filter { !it.isWhitespace() }.length < password.length) {
        makeShortToast(context, "Password can't have whitespace")
        return false
    }
    if (password.length < 5) {
        makeShortToast(context, "Password has to be at least 5 characters long")
        return false
    }
    if (password != repeatPassword) {
        makeShortToast(context, "Password and repeat password don't match")
        return false
    }
    return true
}