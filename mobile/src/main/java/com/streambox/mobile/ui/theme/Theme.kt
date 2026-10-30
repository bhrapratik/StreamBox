package com.streambox.mobile.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.streambox.core.design.BrandPalette

object StreamColors {
    val Background = Color(BrandPalette.Background)
    val Surface = Color(BrandPalette.Surface)
    val Primary = Color(BrandPalette.Primary)
    val OnBackground = Color.White
    val Muted = Color(BrandPalette.Muted)
}

private val colors = darkColorScheme(
    primary = StreamColors.Primary,
    onPrimary = Color.Black,
    background = StreamColors.Background,
    onBackground = StreamColors.OnBackground,
    surface = StreamColors.Background,
    onSurface = StreamColors.OnBackground,
    surfaceVariant = StreamColors.Surface,
    onSurfaceVariant = StreamColors.Muted,
    surfaceContainer = StreamColors.Background, // NavigationBar background
)

@Composable
fun StreamBoxTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colors, content = content)
}