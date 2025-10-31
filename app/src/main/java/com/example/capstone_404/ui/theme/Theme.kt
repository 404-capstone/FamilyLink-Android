package com.example.capstone_404.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Color 설정
private val LightColorScheme = lightColorScheme(
    primary = Main,
    secondary = Sub,
    error = Error,
    background = Background,
    surface = Background,
    onPrimary = TextWhite,
    onSecondary = TextBlack,
    onBackground = TextBlack,
    onSurface = TextBlack
)

@Composable
fun Capstone_404Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}