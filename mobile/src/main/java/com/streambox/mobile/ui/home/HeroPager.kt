package com.streambox.mobile.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.streambox.core.model.Content
import com.streambox.mobile.ui.theme.StreamColors
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HeroPager(
    items: List<Content>,
    onPlay: (Content) -> Unit,
    onInfo: (Content) -> Unit,
    onAdd: (Content) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { items.size }

    // Auto-advance every 6 s. Key = currentPage, so a manual swipe restarts the timer.
    LaunchedEffect(pagerState.currentPage, items.size) {
        if (items.size < 2) return@LaunchedEffect
        delay(6_000.milliseconds)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % items.size)
    }

    Column(modifier) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f),
        ) { page ->
            HeroItem(items[page], onPlay, onInfo, onAdd)
        }
        Spacer(Modifier.height(12.dp))
        PagerDots(count = items.size, current = pagerState.currentPage)
    }
}

@Composable
private fun HeroItem(
    item: Content,
    onPlay: (Content) -> Unit,
    onInfo: (Content) -> Unit,
    onAdd: (Content) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = item.posterUrl ?: item.backdropUrl,   // portrait art fits 3:4 better
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // Scrim: makes white text readable on any image
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0.4f to Color.Transparent,
                    1f to StreamColors.Background,
                )
            )
        )
        Column(
            Modifier.align(Alignment.BottomCenter).padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { onPlay(item) },
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp),
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Play", style = MaterialTheme.typography.titleMedium)
                }
                RoundIconButton(onClick = { onInfo(item) }) { Icon(Icons.Outlined.Info, "Info") }
                RoundIconButton(onClick = { onAdd(item) }) { Icon(Icons.Filled.Add, "Add to my list") }
            }
        }
    }
}

@Composable
private fun RoundIconButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(52.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White,
        ),
        content = content,
    )
}

@Composable
private fun PagerDots(count: Int, current: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(count) { index ->
            val width by animateDpAsState(if (index == current) 24.dp else 8.dp, label = "dot")
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .size(width = width, height = 8.dp)
                    .background(
                        if (index == current) StreamColors.Primary else StreamColors.Muted.copy(alpha = 0.5f),
                        CircleShape,
                    )
            )
        }
    }
}