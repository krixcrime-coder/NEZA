package com.kaizen.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Core palette
val NeonBlue = Color(0xFF4D8CFF)
val NeonPurple = Color(0xFFB24DFF)
val AmoledBlack = Color(0xFF000000)
val SurfaceDark = Color(0xFF0B0B14)
val SurfaceElevated = Color(0xFF14141F)
val TextPrimary = Color(0xFFF2F2F7)
val TextSecondary = Color(0xFF9A9AAE)

private val KaizenDarkScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = NeonPurple,
    tertiary = NeonPurple,
    background = AmoledBlack,
    surface = SurfaceDark,
    surfaceVariant = SurfaceElevated,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = Color(0xFFFF5470)
)

@Composable
fun KaizenAITheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Kaizen AI is AMOLED-dark by design; light theme intentionally not offered yet.
    MaterialTheme(
        colorScheme = KaizenDarkScheme,
        typography = KaizenTypography,
        content = content
    )
}
