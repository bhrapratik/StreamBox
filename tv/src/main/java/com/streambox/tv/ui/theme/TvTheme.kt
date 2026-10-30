package com.streambox.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.streambox.core.design.BrandPalette

object TvColors {
    val Background = Color(BrandPalette.Background)
    val Surface = Color(BrandPalette.Surface)
    val Primary = Color(BrandPalette.Primary)
    val Muted = Color(BrandPalette.Muted)
}

@Composable
fun StreamBoxTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = TvColors.Primary,
            onPrimary = Color.Black,
            background = TvColors.Background,
            onBackground = Color.White,
            surface = TvColors.Background,
            onSurface = Color.White,
            surfaceVariant = TvColors.Surface,
            onSurfaceVariant = TvColors.Muted,
        ),
        content = content,
    )
}