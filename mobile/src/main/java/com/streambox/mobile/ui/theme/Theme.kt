package com.streambox.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.streambox.core.design.AppTheme

data class StreamPalette(
    val background: Color,
    val surface: Color,
    val primary: Color,
    val muted: Color,
)

fun AppTheme.toPalette() = StreamPalette(
    background = Color(background),
    surface = Color(surface),
    primary = Color(primary),
    muted = Color(muted),
)

private val LocalStreamPalette = staticCompositionLocalOf { AppTheme.Default.toPalette() }

/** Same call sites as before (StreamColors.Primary), but now reads the current theme. */
object StreamColors {
    val Background: Color @Composable @ReadOnlyComposable get() = LocalStreamPalette.current.background
    val Surface: Color @Composable @ReadOnlyComposable get() = LocalStreamPalette.current.surface
    val Primary: Color @Composable @ReadOnlyComposable get() = LocalStreamPalette.current.primary
    val Muted: Color @Composable @ReadOnlyComposable get() = LocalStreamPalette.current.muted
    val OnBackground = Color.White
}

@Composable
fun StreamBoxTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val palette = remember(theme) { theme.toPalette() }
    val onPrimary =
        if (palette.primary.luminance() > 0.5f) Color.Black else Color.White  // readable on amber/teal

    val colors = darkColorScheme(
        primary = palette.primary,
        onPrimary = onPrimary,
        background = palette.background,
        onBackground = Color.White,
        surface = palette.background,
        onSurface = Color.White,
        surfaceVariant = palette.surface,
        onSurfaceVariant = palette.muted,
        surfaceContainer = palette.background,
    )

    CompositionLocalProvider(LocalStreamPalette provides palette) {
        MaterialTheme(colorScheme = colors, content = content)
    }
}