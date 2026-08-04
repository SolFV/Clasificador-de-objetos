package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val IndustrialDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = NeonCyanBg,
    onPrimaryContainer = TextPrimary,
    secondary = NeonGreen,
    onSecondary = Color.Black,
    secondaryContainer = NeonGreenBg,
    onSecondaryContainer = TextPrimary,
    tertiary = NeonAmber,
    onTertiary = Color.Black,
    tertiaryContainer = NeonAmberBg,
    onTertiaryContainer = TextPrimary,
    error = NeonRed,
    onError = Color.Black,
    errorContainer = NeonRedBg,
    onErrorContainer = TextPrimary,
    background = SlateDarkBackground,
    onBackground = TextPrimary,
    surface = SlateCardSurface,
    onSurface = TextPrimary,
    surfaceVariant = SlateCardBorder,
    onSurfaceVariant = TextSecondary
)

@Composable
fun ConveyorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = IndustrialDarkColorScheme,
        typography = Typography,
        content = content
    )
}

