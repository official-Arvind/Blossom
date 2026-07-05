package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ImmersiveText,
    secondary = TextSecondary,
    tertiary = YouTubeRed,
    background = ImmersiveBackground,
    surface = ImmersiveSurface,
    surfaceVariant = PlayerBackground,
    onPrimary = ImmersiveBackground,
    onSecondary = ImmersiveText,
    onBackground = ImmersiveText,
    onSurface = ImmersiveText,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme, // Force dark theme
        typography = Typography,
        content = content
    )
}
