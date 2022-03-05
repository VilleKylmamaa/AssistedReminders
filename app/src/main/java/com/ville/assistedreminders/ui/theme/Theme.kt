package com.ville.assistedreminders.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.ville.assistedreminders.ui.theme.ThemeState.darkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color


object ThemeState {
    var darkTheme by mutableStateOf(true)
}

val Colors.reminderMessage: Color
    get() = if (darkTheme) LightPink else Pink

val Colors.reminderIcon: Color
    get() = if (darkTheme) LightGreen else Teal500

val Colors.secondaryButtonBackground: Color
    get() = if (darkTheme) LightBlue else DarkBlue

val Colors.showAllButtonBackground: Color
    get() = if (darkTheme) LightOrange else DarkOrange

@Composable
fun AssistedRemindersTheme(
        content: @Composable () -> Unit
    ) {
    val colors = if (darkTheme) {
        darkColors(
            background = DarkGrey,
            primary = Teal200,
            primaryVariant = Teal700,
            secondaryVariant = Teal100,
            onPrimary = Black,
            onSecondary = Color.White
        )
    } else {
        lightColors(
            primary = Green200,
            primaryVariant = Green700,
            secondaryVariant = Green100,
            onPrimary = Black,
            onSecondary = Black
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}