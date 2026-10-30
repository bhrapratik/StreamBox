package com.streambox.mobile.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.streambox.core.model.Content
import com.streambox.core.model.HomeFeed
import com.streambox.core.ui.home.HomeUiState
import com.streambox.core.ui.home.HomeViewModel

@Composable
fun HomeScreen(
    onContentClick: (Content) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val s = state) {
        HomeUiState.Loading -> Box(modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        is HomeUiState.Error -> Column(
            modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(s.message)
            Spacer(Modifier.height(12.dp))
            Button(onClick = viewModel::retry) { Text("Try again") }
        }
        is HomeUiState.Success -> HomeContent(s.feed, onContentClick, modifier)
    }
}

@Composable
private fun HomeContent(feed: HomeFeed, onContentClick: (Content) -> Unit, modifier: Modifier) {
    LazyColumn(modifier.fillMaxSize()) {
        if (feed.hero.isNotEmpty()) {
            item(key = "hero", contentType = "hero") {
                HeroPager(
                    items = feed.hero,
                    onPlay = onContentClick,
                    onInfo = onContentClick,
                    onAdd = { /* Step 9: My list */ },
                )
            }
        }
        items(feed.rails, key = { it.id }, contentType = { it.style }) { rail ->
            RailRow(rail, onItemClick = onContentClick)
        }
        if (!feed.isComplete) {
            item(key = "loading_more") {
                Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(28.dp))
                }
            }
        }
    }
}