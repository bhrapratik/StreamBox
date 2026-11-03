package com.streambox.uimobile.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streambox.core.design.AppTheme
import com.streambox.core.ui.setting.SettingsViewModel
import com.streambox.core.vehicle.VehicleState
import com.streambox.core.vehicle.VehicleViewModel
import com.streambox.uimobile.ui.theme.StreamColors


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    vehicleViewModel: VehicleViewModel = hiltViewModel(),
) {
    val current by viewModel.theme.collectAsStateWithLifecycle()
    val vehicle by vehicleViewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        vehicle?.let {
            SectionTitle("Vehicle")
            VehicleSection(it)
            Spacer(Modifier.height(24.dp))
        }
        SectionTitle("Theme")
        AppTheme.entries.forEach { theme ->
            ThemeRow(
                theme = theme,
                selected = theme == current,
                onClick = { viewModel.setTheme(theme) },
            )
        }

        Spacer(Modifier.height(24.dp))
        SectionTitle("About")
        AboutText()
    }
}

@Composable
private fun ThemeRow(theme: AppTheme, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThemeSwatch(theme)
        Spacer(Modifier.width(16.dp))
        Text(theme.label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Composable
fun ThemeSwatch(theme: AppTheme) {
    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {   // overlapping circles
        listOf(theme.background, theme.surface, theme.primary).forEach { color ->
            Box(
                Modifier
                    .size(28.dp)
                    .background(Color(color), CircleShape)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)  // subtle edge on dark colors
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun AboutText() {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("StreamBox — Android mobile + TV learning project.")
        Text("Movie and TV data and images: The Movie Database (TMDB).", color = StreamColors.Muted)
        Text("This product uses the TMDB API but is not endorsed or certified by TMDB.", color = StreamColors.Muted)
        Text("TV schedule data: TVmaze (CC BY-SA).", color = StreamColors.Muted)
        Text("Video: public test streams.", color = StreamColors.Muted)
    }
}

@Composable
private fun VehicleSection(state: VehicleState) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Gear: ${state.gear ?: "unknown"}")
        Text("Speed: ${state.speedKmh?.let { "%.0f km/h".format(it) } ?: "unknown"}")
        Text(
            if (state.drivingRestricted) "Driving: video is paused"
            else "Parked or idling: video available",
            color = StreamColors.Muted,
        )
    }
}