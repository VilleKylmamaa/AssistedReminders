package com.ville.assistedreminders.ui.login

import android.content.Context
import android.text.TextUtils
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.systemBarsPadding
import com.ville.assistedreminders.data.entity.Account
import com.ville.assistedreminders.util.makeShortToast
import kotlinx.coroutines.launch


@Composable
fun Signup(
    navController: NavController,
    viewModel: LoginViewModel = viewModel(),
    onBackPress: () -> Unit
) {
    val username = rememberSaveable {mutableStateOf("")}
    val password = rememberSaveable {mutableStateOf("")}
    val confirmPassword = rememberSaveable {mutableStateOf("")}
    val email = rememberSaveable {mutableStateOf("")}
    val coroutineScope = rememberCoroutineScope()
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
                text = "Register New Account",
                fontSize = 26.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = email.value,
                onValueChange = { data -> email.value = data },
                label = {Text("Your email address")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(18.dp))
            OutlinedTextField(
                value = username.value,
                onValueChange = { data -> username.value = data },
                label = {Text("Choose username")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(18.dp))
            OutlinedTextField(
                value = password.value,
                onValueChange = { data -> password.value = data },
                label = {Text("Choose password")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(18.dp))
            OutlinedTextField(
                value = confirmPassword.value,
                onValueChange = { data -> confirmPassword.value = data },
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
                    coroutineScope.launch {
                        // Check that the given username, password and repeat password are valid
                        if (isValidSignupDetails(
                            username.value, password.value, confirmPassword.value, context)) {
                            if (isValidEmail(email.value)) {
                                // Check if the chosen username already exists
                                val usernameAlreadyExists = viewModel.findAccount(username.value)
                                if (usernameAlreadyExists == null
                                    || usernameAlreadyExists.username != username.value
                                ) {
                                    val newAccount = Account(
                                        username = username.value,
                                        password = password.value,
                                        email = email.value,
                                        isLoggedIn = true
                                    )
                                    viewModel.addAccount(newAccount)
                                    navController.navigate("home")
                                    makeShortToast(
                                        context, "New account created!" +
                                                " Logged in as: ${username.value}"
                                    )
                                } else {
                                    makeShortToast(context, "Account with this username already exists")
                                }
                            } else {
                                makeShortToast(context, "Enter a valid email address")
                            }
                        }
                    }
                },
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 16.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Signup! :D",
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

private fun isValidSignupDetails(username: String, password: String, repeatPassword: String,
    context: Context): Boolean {
    if (username.filter { !it.isWhitespace() }.length < username.length
        || password.filter { !it.isWhitespace() }.length < password.length) {
            makeShortToast(context, "Username and password can't have whitespace")
            return false
    }
    if (username.length < 0 || password.length < 0) {
        makeShortToast(context, "Username and password have to be at least 5 characters long")
        return false
    }
    if (password != repeatPassword) {
        makeShortToast(context, "Password and repeat password don't match")
        return false
    }
    return true
}

fun isValidEmail(target: CharSequence): Boolean {
    return true
    return if (TextUtils.isEmpty(target)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}