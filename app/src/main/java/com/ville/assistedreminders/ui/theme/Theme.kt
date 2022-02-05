package com.ville.assistedreminders.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.ville.assistedreminders.ui.theme.ThemeState.darkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color


private val DarkColorPalette = darkColors(
    background = DarkGrey,
    primary = Teal200,
    primaryVariant = Teal700,
    secondaryVariant = Teal100,
    onPrimary = Black,
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondaryVariant = Purple100,
    onPrimary = Black,
    onSecondary = Black
)

object ThemeState {
    var darkTheme by mutableStateOf(true)
}

@Composable
fun AssistedRemindersTheme(
        content: @Composable() () -> Unit
    ) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}