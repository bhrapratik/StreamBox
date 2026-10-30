package com.streambox.tv.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

import com.streambox.core.model.Content
import com.streambox.core.model.HomeFeed
import com.streambox.core.model.Rail
import com.streambox.core.model.RailStyle
import com.streambox.core.ui.home.HomeUiState
import com.streambox.core.ui.home.HomeViewModel
import com.streambox.tv.ui.focus.PinFocusedItem
import com.streambox.tv.ui.focus.rememberKeyRepeatThrottle
import com.streambox.tv.ui.theme.TvColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TvHomeScreen(
    onBackdropChange: (String?) -> Unit,
    onContentClick: (Content) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        HomeUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Loading…")
        }
        is HomeUiState.Error -> Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(s.message)
            Spacer(Modifier.height(16.dp))
            Button(onClick = viewModel::retry) { Text("Try again") }
        }
        is HomeUiState.Success -> TvHomeContent(s.feed, onBackdropChange, onContentClick)
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TvHomeContent(
    feed: HomeFeed,
    onBackdropChange: (String?) -> Unit,
    onContentClick: (Content) -> Unit,
) {
    val rails = remember(feed) {
        buildList {
            if (feed.hero.isNotEmpty()) add(Rail("featured", "Featured", feed.hero, RailStyle.Landscape))
            addAll(feed.rails)
        }
    }

    // ---- Focus-driven hero ----
    var focused by remember { mutableStateOf<Content?>(null) }
    val shown = focused ?: feed.hero.firstOrNull()

    LaunchedEffect(shown?.id) {
        delay(300)
        shown?.backdropUrl?.let(onBackdropChange)
    }

    // ---- 5.2 Hoisted scroll state ----
    val columnState = rememberLazyListState()
    val firstRowState = rememberLazyListState()

    var lastFocusedKey by rememberSaveable { mutableStateOf<String?>(null) }
    val restoreFocus = remember { FocusRequester() }
    // ---- Initial focus ----
    val firstCard = remember { FocusRequester() }
    var initialFocusDone by remember { mutableStateOf(false) }

    LaunchedEffect(rails.isNotEmpty()) {
        if (rails.isEmpty() || initialFocusDone) return@LaunchedEffect
        withFrameNanos { }                                   // let the restored scroll positions compose
        initialFocusDone =
            (lastFocusedKey != null && runCatching { restoreFocus.requestFocus() }.isSuccess) ||
                    runCatching { firstCard.requestFocus() }.isSuccess
    }

    // ---- 5.4 Back: anywhere deep in content → back to the first card ----
    var contentHasFocus by remember { mutableStateOf(false) }
    val firstItemId = rails.firstOrNull()?.items?.firstOrNull()?.id
    val isAtStart = focused == null || focused?.id == firstItemId
    val scope = rememberCoroutineScope()

    BackHandler(enabled = contentHasFocus && !isAtStart) {
        scope.launch {
            columnState.scrollToItem(0)
            firstRowState.scrollToItem(0)
            withFrameNanos { }                       // wait one frame so the first card is composed
            runCatching { firstCard.requestFocus() }
        }
    }

    // ---- 5.1 Pin focused rail under the hero info (rows keep the default spec) ----
    val defaultSpec = LocalBringIntoViewSpec.current
    val pinSpec = remember { PinFocusedItem(parentFraction = 0f) }

    // ---- 5.5 Throttle key repeats ----
    val keyThrottle = rememberKeyRepeatThrottle()

    Column(Modifier.fillMaxSize()) {
        HeroInfo(
            item = shown,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(start = 24.dp, top = 40.dp, end = 48.dp),
        )

        CompositionLocalProvider(LocalBringIntoViewSpec provides pinSpec) {
            LazyColumn(
                state = columnState,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { contentHasFocus = it.hasFocus }
                    .then(keyThrottle)
                    .focusRestorer(),
                // Extra bottom space so even the LAST rail can scroll up to the pinned position
                contentPadding = PaddingValues(bottom = 240.dp),
            ) {
                itemsIndexed(rails, key = { _, rail -> rail.id }, contentType = { _, rail -> rail.style }) { railIndex, rail ->
                    // Rows scroll horizontally with the normal behaviour, not the vertical pin
                    CompositionLocalProvider(LocalBringIntoViewSpec provides defaultSpec) {
                        TvRailRow(
                            rail = rail,
                            listState = if (railIndex == 0) firstRowState else null,
                            onItemClick = onContentClick,
                            onItemFocused = { item ->
                                focused = item
                                lastFocusedKey = "${rail.id}/${item.id}"
                            },
                            itemModifier = { index, item ->
                                var m: Modifier = Modifier
                                if (railIndex == 0 && index == 0) m = m.focusRequester(firstCard)
                                if ("${rail.id}/${item.id}" == lastFocusedKey) m = m.focusRequester(restoreFocus)
                                m
                            },
                        )
                    }
                }
                if (!feed.isComplete) {
                    item(key = "loading_more") {
                        Text(
                            "Loading more…",
                            color = TvColors.Muted,
                            modifier = Modifier.padding(start = 24.dp, top = 12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroInfo(item: Content?, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.Bottom) {
        if (item == null) return@Column
        Text(
            text = item.title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        val meta = listOfNotNull(
            item.year,
            item.rating?.let { "★ %.1f".format(it) },
            item.badge,
        ).joinToString("  ·  ")
        if (meta.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(meta, style = MaterialTheme.typography.bodyMedium, color = TvColors.Muted)
        }
        if (item.description.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(0.55f),
            )
        }
    }
}