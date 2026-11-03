package com.streambox.tv.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.streambox.core.design.AppTheme

data class TvPalette(val background: Color, val surface: Color, val primary: Color, val muted: Color)

fun AppTheme.toTvPalette() = TvPalette(Color(background), Color(surface), Color(primary), Color(muted))

private val LocalTvPalette = staticCompositionLocalOf { AppTheme.Default.toTvPalette() }

object TvColors {
    val Background: Color @Composable @ReadOnlyComposable get() = LocalTvPalette.current.background
    val Surface: Color @Composable @ReadOnlyComposable get() = LocalTvPalette.current.surface
    val Primary: Color @Composable @ReadOnlyComposable get() = LocalTvPalette.current.primary
    val Muted: Color @Composable @ReadOnlyComposable get() = LocalTvPalette.current.muted
}

@Composable
fun StreamBoxTvTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val palette = remember(theme) { theme.toTvPalette() }
    val onPrimary = if (palette.primary.luminance() > 0.5f) Color.Black else Color.White

    CompositionLocalProvider(LocalTvPalette provides palette) {
        MaterialTheme(
            colorScheme = darkColorScheme(
                primary = palette.primary,
                onPrimary = onPrimary,
                background = palette.background,
                onBackground = Color.White,
                surface = palette.background,
                onSurface = Color.White,
                surfaceVariant = palette.surface,
                onSurfaceVariant = palette.muted,
            ),
            content = content,
        )
    }
}