package com.streambox.tv.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.streambox.core.model.Content
import com.streambox.tv.ui.theme.TvColors

private val TvCardShape = RoundedCornerShape(8.dp)

@Composable
private fun tvCardScale() = CardDefaults.scale(focusedScale = 1.1f)

@Composable
private fun tvCardBorder() = CardDefaults.border(
    focusedBorder = Border(BorderStroke(2.dp, Color.White), shape = TvCardShape),
)

@Composable
private fun tvCardColors() = CardDefaults.colors(containerColor = TvColors.Surface)

@Composable
fun TvPosterCard(
    content: Content,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .width(120.dp)
            .aspectRatio(2f / 3f)
            .onFocusChanged { if (it.isFocused) onFocused() },
        shape = CardDefaults.shape(TvCardShape),
        scale = tvCardScale(),
        border = tvCardBorder(),
        colors = tvCardColors(),
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = content.posterUrl,
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            content.badge?.let { TvBadge(it, Modifier.align(Alignment.TopStart)) }
        }
    }
}

@Composable
fun TvLandscapeCard(
    content: Content,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.width(196.dp)) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .onFocusChanged { if (it.isFocused) onFocused() },
            shape = CardDefaults.shape(TvCardShape),
            scale = tvCardScale(),
            border = tvCardBorder(),
            colors = tvCardColors(),
        ) {
            AsyncImage(
                model = content.backdropUrl ?: content.posterUrl,
                contentDescription = content.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        content.badge?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = TvColors.Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun TvLogoCard(
    content: Content,
    onClick: () -> Unit,
    onFocused: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .size(width = 140.dp, height = 80.dp)
            .onFocusChanged { if (it.isFocused) onFocused() },
        shape = CardDefaults.shape(TvCardShape),
        scale = tvCardScale(),
        border = tvCardBorder(),
        colors = tvCardColors(),
    ) {
        AsyncImage(
            model = content.posterUrl,
            contentDescription = content.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().padding(16.dp),
        )
    }
}

@Composable
private fun TvBadge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = modifier
            .background(TvColors.Primary, RoundedCornerShape(bottomEnd = 6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
    )
}