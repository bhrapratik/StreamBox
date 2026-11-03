package com.streambox.mobile.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.streambox.core.model.Content
import com.streambox.core.ui.search.SearchUiState
import com.streambox.core.ui.search.SearchViewModel
import com.streambox.mobile.ui.components.PosterCard
import com.streambox.mobile.ui.theme.StreamColors

@Composable
fun SearchScreen(
    onContentClick: (Content) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current
    val fieldFocus = remember { FocusRequester() }

    // Open the keyboard when the tab is opened
    LaunchedEffect(Unit) { fieldFocus.requestFocus() }

    Column(modifier.fillMaxSize()) {
        OutlinedTextField(
            value = viewModel.query,
            onValueChange = viewModel::onQueryChange,
            placeholder = { Text("Movies and series") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            trailingIcon = {
                if (viewModel.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .focusRequester(fieldFocus),
        )

        when (val s = state) {
            SearchUiState.Idle -> Message("Search for movies and series")
            SearchUiState.Loading -> Box(
                Modifier.fillMaxSize(),
                Alignment.Center
            ) { CircularProgressIndicator() }

            is SearchUiState.Empty -> Message("No results for \"${s.query}\"")
            is SearchUiState.Error -> Message(s.message)
            is SearchUiState.Results -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 110.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(s.items, key = { it.id }) { item ->
                    PosterCard(
                        content = item,
                        onClick = {
                            keyboard?.hide()
                            onContentClick(item)
                        },
                        modifier = Modifier.fillMaxWidth(),   // overrides the card's fixed 130.dp width
                    )
                }
            }
        }
    }
}

@Composable
private fun Message(text: String) {
    Box(Modifier
        .fillMaxSize()
        .padding(32.dp), Alignment.Center) {
        Text(text, color = StreamColors.Muted)
    }
}