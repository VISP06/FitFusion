package com.example.fitfusion.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = BeigeAccent,
    primaryContainer = NavyDeep,
    onPrimaryContainer = BeigeAccent,
    secondary = CoralMuted,
    onSecondary = BeigeAccent,
    background = BackgroundTeal,
    onBackground = BeigeAccent,
    surface = SurfaceBeige,
    onSurface = OnSurfaceNavy,
    outline = NavyDeep
)

// Retro-modern Japanese aesthetic: Zero rounded corners
val RetroShapes = Shapes(
    small = RectangleShape,
    medium = RectangleShape,
    large = RectangleShape
)

@Composable
fun FitFusionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+, but we want our custom aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // Staying with the custom light palette for the aesthetic
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = RetroShapes,
        content = content
    )
}
