package com.scet.garagex_application.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// =========================================================================
// Light Theme: Deep Burgundy accents on Warm Cream Beige canvas
// =========================================================================
private val LightColorScheme = lightColorScheme(
    primary = DeepBurgundyPrimary,
    onPrimary = DeepBurgundyOnPrimary,
    primaryContainer = DeepBurgundyContainer,
    onPrimaryContainer = DeepBurgundyOnContainer,

    secondary = MutedSageSecondary,
    onSecondary = MutedSageOnSecondary,
    secondaryContainer = MutedSageContainer,
    onSecondaryContainer = MutedSageOnContainer,

    background = WarmCreamBackground,
    onBackground = TextPrimary,
    surface = WarmCreamSurface,
    onSurface = TextPrimary,
    surfaceVariant = WarmCreamSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = WarmCreamOutline,
    error = StatusErrorRed
)

// =========================================================================
// Dark Theme: Rich muted night tones
// =========================================================================
private val DarkColorScheme = darkColorScheme(
    primary = DarkBurgundyPrimary,
    onPrimary = DarkBurgundyOnPrimary,
    secondary = DarkSageSecondary,
    background = DarkCreamBackground,
    onBackground = WarmCreamSurface,
    surface = DarkCreamSurface,
    onSurface = WarmCreamSurface
)

/**
 * GarejXTheme wraps all our Composables and applies the Atelier Automotive
 * design system (colors, typography, and status bar behavior).
 */
@Composable
fun GarejXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Synchronize system status bar color with our background
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
