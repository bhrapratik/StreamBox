package com.streambox.tv.ui.home.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.streambox.core.model.ContentDetails
import com.streambox.core.model.metaLine
import com.streambox.core.ui.home.detail.DetailsUiState
import com.streambox.core.ui.home.detail.DetailsViewModel
import com.streambox.tv.ui.theme.TvColors

@Composable
fun TvDetailsScreen(
    onPlay: (ContentDetails) -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        DetailsUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Loading…") }
        is DetailsUiState.Error -> Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(s.message)
            Spacer(Modifier.height(16.dp))
            Button(onClick = viewModel::retry) { Text("Try again") }
        }
        is DetailsUiState.Success -> TvDetailsContent(s.details, onPlay)
    }
}

@Composable
private fun TvDetailsContent(details: ContentDetails, onPlay: (ContentDetails) -> Unit) {
    val playFocus = remember { FocusRequester() }
    LaunchedEffect(details.id) { runCatching { playFocus.requestFocus() } }

    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = details.backdropUrl ?: details.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.horizontalGradient(0f to TvColors.Background, 0.65f to Color.Transparent)
            )
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(0.6f to Color.Transparent, 1f to TvColors.Background)
            )
        )

        Column(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.55f)
                .padding(start = 48.dp, end = 24.dp, top = 27.dp, bottom = 27.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                details.title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(8.dp))
            Text(details.metaLine(), style = MaterialTheme.typography.titleMedium, color = TvColors.Muted)

            if (details.genres.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(details.genres.joinToString(" · "), style = MaterialTheme.typography.bodyMedium, color = TvColors.Muted)
            }

            Spacer(Modifier.height(16.dp))
            Text(
                details.overview,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )

            if (details.cast.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    "Cast: " + details.cast.take(5).joinToString { it.name },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TvColors.Muted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { onPlay(details) },
                    modifier = Modifier.focusRequester(playFocus),
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Play")
                }
                OutlinedButton(onClick = { /* Step 9 */ }) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("My list")
                }
            }
        }
    }
}