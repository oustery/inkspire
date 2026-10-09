package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = InkGoldPrimary,
    onPrimary = InkGoldOnPrimary,
    primaryContainer = InkGoldContainer,
    onPrimaryContainer = InkGoldOnContainer,
    secondary = InkCrimsonSecondary,
    onSecondary = InkCrimsonOnSecondary,
    secondaryContainer = InkCrimsonContainer,
    onSecondaryContainer = InkCrimsonOnContainer,
    tertiary = InkVioletTertiary,
    onTertiary = InkVioletOnTertiary,
    tertiaryContainer = InkVioletContainer,
    onTertiaryContainer = InkVioletOnContainer,
    background = InkBackground,
    onBackground = InkOnBackground,
    surface = InkSurface,
    onSurface = InkOnSurface,
    surfaceVariant = InkSurfaceVariant,
    onSurfaceVariant = InkOnSurfaceVariant,
    outline = InkOutline,
    outlineVariant = InkOutlineVariant
)

// We maintain a dark, immersive tattoo atelier vibe by default
private val LightColorScheme = DarkColorScheme

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
