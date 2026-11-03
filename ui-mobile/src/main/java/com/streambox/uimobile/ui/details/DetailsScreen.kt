package com.streambox.uimobile.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streambox.core.ui.home.detail.DetailsUiState
import com.streambox.core.ui.home.detail.DetailsViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.streambox.core.model.CastMember
import com.streambox.core.model.ContentDetails
import com.streambox.core.model.metaLine
import com.streambox.uimobile.ui.adaptive.isWideLayout
import com.streambox.uimobile.ui.theme.StreamColors


@Composable
fun DetailsScreen(
    onBack: () -> Unit,
    onPlay: (ContentDetails) -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val s = state) {
        DetailsUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }

        is DetailsUiState.Error -> Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(s.message)
            Spacer(Modifier.height(12.dp))
            Button(onClick = viewModel::retry) { Text("Try again") }
        }

        is DetailsUiState.Success ->  if (isWideLayout()) WideDetails(s.details, onBack, onPlay)
        else DetailsContent(s.details, onBack, onPlay)
    }
}

@Composable
private fun DetailsContent(
    details: ContentDetails,
    onBack: () -> Unit,
    onPlay: (ContentDetails) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize()) {
        // Header
        item(key = "header") {
            Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
                AsyncImage(
                    model = details.backdropUrl ?: details.posterUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    Modifier.fillMaxSize().background(
                        Brush.verticalGradient(0.5f to Color.Transparent, 1f to StreamColors.Background)
                    )
                )
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }

        // Info + actions
        item(key = "info") {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(details.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(details.metaLine(), style = MaterialTheme.typography.bodyMedium, color = StreamColors.Muted)
                Spacer(Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { onPlay(details) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Play")
                    }
                    OutlinedButton(onClick = { /* Step 9 */ }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("My list")
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(details.overview, style = MaterialTheme.typography.bodyLarge)

                if (details.genres.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        details.genres.joinToString(" · "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = StreamColors.Muted,
                    )
                }
            }
        }

        // Cast
        if (details.cast.isNotEmpty()) {
            item(key = "cast") {
                Text(
                    "Cast",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 10.dp),
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(details.cast, key = { it.id }) { CastItem(it) }
                }
                Spacer(Modifier.navigationBarsPadding().height(24.dp))
            }
        }
    }
}

@Composable
private fun WideDetails(
    details: ContentDetails,
    onBack: () -> Unit,
    onPlay: (ContentDetails) -> Unit,
) {
    Row(Modifier.fillMaxSize()) {
        // Left: backdrop
        Box(Modifier.weight(1.1f).fillMaxHeight()) {
            AsyncImage(
                model = details.backdropUrl ?: details.posterUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(Modifier.fillMaxSize().background(
                Brush.horizontalGradient(0.6f to Color.Transparent, 1f to StreamColors.Background)))
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(12.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape),
            ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
        }
        // Right: info, scrollable
        Column(
            Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()).padding(24.dp),
        ) {
            Text(details.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(details.metaLine(), color = StreamColors.Muted)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onPlay(details) }) {
                    Icon(Icons.Filled.PlayArrow, null); Spacer(Modifier.width(6.dp)); Text("Play")
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(details.overview, style = MaterialTheme.typography.bodyLarge)
            if (details.genres.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text(details.genres.joinToString(" · "), color = StreamColors.Muted)
            }
            if (details.cast.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("Cast: " + details.cast.take(6).joinToString { it.name }, color = StreamColors.Muted)
            }
        }
    }
}

@Composable
private fun CastItem(member: CastMember) {
    Column(Modifier.width(80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = member.photoUrl,
            contentDescription = member.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(StreamColors.Surface),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            member.name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}