package com.streambox.uimobile.ui.home

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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.streambox.core.model.Content
import com.streambox.uimobile.ui.adaptive.isWideLayout
import com.streambox.uimobile.ui.theme.StreamColors
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
    val wide = isWideLayout()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val shortScreen = screenHeight < 480.dp            // phone in landscape

    // Auto-advance every 6 s. Key = currentPage, so a manual swipe restarts the timer.
    LaunchedEffect(pagerState.currentPage, items.size) {
        if (items.size < 2) return@LaunchedEffect
        delay(6_000.milliseconds)
        pagerState.animateScrollToPage((pagerState.currentPage + 1) % items.size)
    }

    Column(modifier) {
        Box {
            HorizontalPager(
                state = pagerState,
                modifier = if (wide) {
                    // ~40% of screen height → rails stay visible on car / landscape
                    Modifier.fillMaxWidth().height((screenHeight * 0.42f).coerceAtLeast(200.dp))
                } else {
                    Modifier.fillMaxWidth().aspectRatio(3f / 4f)
                },
            ) { page ->
                HeroItem(
                    item = items[page],
                    wide = wide,
                    showDescription = wide && !shortScreen,
                    onPlay = onPlay,
                    onInfo = onInfo,
                    onAdd = onAdd,
                )
            }
            if (wide) {
                PagerDots(
                    count = items.size,
                    current = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 12.dp),
                )
            }
        }
        if (!wide) {
            Spacer(Modifier.height(12.dp))
            PagerDots(
                count = items.size,
                current = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HeroItem(
    item: Content,
    wide: Boolean,
    showDescription: Boolean,
    onPlay: (Content) -> Unit,
    onInfo: (Content) -> Unit,
    onAdd: (Content) -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = if (wide) item.backdropUrl ?: item.posterUrl else item.posterUrl ?: item.backdropUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            alignment = if (wide) Alignment.CenterEnd else Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        )

        // Scrim: left-to-right when wide, so text sits on the dark side
        Box(
            Modifier.fillMaxSize().background(
                if (wide) Brush.horizontalGradient(0f to StreamColors.Background, 0.55f to Color.Transparent)
                else Brush.verticalGradient(0.4f to Color.Transparent, 1f to StreamColors.Background)
            )
        )

        Column(
            Modifier
                .align(if (wide) Alignment.CenterStart else Alignment.BottomCenter)
                .fillMaxWidth(if (wide) 0.45f else 1f)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = if (wide) Alignment.Start else Alignment.CenterHorizontally,
        ) {
            Text(
                text = item.title,
                style = if (wide) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = if (wide) TextAlign.Start else TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (showDescription && item.description.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.description,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = StreamColors.Muted,
                )
            }
            Spacer(Modifier.height(if (wide) 12.dp else 16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(if (wide) 12.dp else 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    onClick = { onPlay(item) },
                    contentPadding = PaddingValues(
                        horizontal = if (wide) 20.dp else 28.dp,
                        vertical = if (wide) 8.dp else 12.dp,
                    ),
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Play", style = MaterialTheme.typography.titleMedium)
                }
                RoundIconButton(size = if (wide) 44 else 52, onClick = { onInfo(item) }) {
                    Icon(Icons.Outlined.Info, "Info")
                }
                RoundIconButton(size = if (wide) 44 else 52, onClick = { onAdd(item) }) {
                    Icon(Icons.Filled.Add, "Add to my list")
                }
            }
        }
    }
}

@Composable
private fun RoundIconButton(size: Int, onClick: () -> Unit, content: @Composable () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(size.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White.copy(alpha = 0.15f),
            contentColor = Color.White,
        ),
        content = content,
    )
}

@Composable
private fun PagerDots(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
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