package com.streambox.tv.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.*
import com.streambox.core.design.AppTheme
import com.streambox.core.ui.setting.SettingsViewModel
import com.streambox.tv.ui.theme.TvColors

@Composable
fun TvProfileScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val current by viewModel.theme.collectAsStateWithLifecycle()

    Row(
        Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 27.dp, end = 48.dp, bottom = 27.dp),
        horizontalArrangement = Arrangement.spacedBy(48.dp),
    ) {
        // Left: theme picker (focusable)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "Theme",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            AppTheme.entries.forEach { theme ->
                ListItem(
                    selected = theme == current,
                    onClick = { viewModel.setTheme(theme) },
                    headlineContent = { Text(theme.label) },
                    leadingContent = { TvThemeSwatch(theme) },
                    trailingContent = {
                        if (theme == current) Icon(
                            Icons.Filled.Check,
                            contentDescription = "Selected"
                        )
                    },
                )
            }
        }

        // Right: about (not focusable, just text)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "About",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("StreamBox — Android mobile + TV learning project.")
            Text("Movie and TV data and images: The Movie Database (TMDB).", color = TvColors.Muted)
            Text(
                "This product uses the TMDB API but is not endorsed or certified by TMDB.",
                color = TvColors.Muted
            )
            Text("TV schedule data: TVmaze (CC BY-SA).", color = TvColors.Muted)
            Text("Video: public test streams.", color = TvColors.Muted)
        }
    }
}

@Composable
private fun TvThemeSwatch(theme: AppTheme) {
    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        listOf(theme.background, theme.surface, theme.primary).forEach { color ->
            Box(Modifier
                .size(24.dp)
                .background(Color(color), CircleShape))
        }
    }
}