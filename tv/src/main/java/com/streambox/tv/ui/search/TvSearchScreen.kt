package com.streambox.tv.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.streambox.core.model.Content
import com.streambox.core.ui.search.SearchUiState
import com.streambox.core.ui.search.SearchViewModel
import com.streambox.tv.ui.components.TvPosterCard
import com.streambox.tv.ui.theme.TvColors

@Composable
fun TvSearchScreen(
    onContentClick: (Content) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 24.dp, top = 27.dp, end = 48.dp),
    ) {
        TvSearchField(
            value = viewModel.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier.fillMaxWidth(0.5f),
        )
        Spacer(Modifier.height(16.dp))

        when (val s = state) {
            SearchUiState.Idle -> TvMessage("Press OK to type, or use the mic on your remote")
            SearchUiState.Loading -> TvMessage("Searching…")
            is SearchUiState.Empty -> TvMessage("No results for \"${s.query}\"")
            is SearchUiState.Error -> TvMessage(s.message)
            is SearchUiState.Results -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 130.dp),
                contentPadding = PaddingValues(vertical = 14.dp),     // room for the 1.1× focus scale
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(s.items, key = { it.id }) { item ->
                    TvPosterCard(
                        content = item,
                        onClick = { onContentClick(item) },
                        onFocused = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
/**
 * TV text field:
 * - focusing it with the D-pad does NOT pop up the keyboard (it would cover the screen
 *   every time you pass it)
 * - pressing OK opens the keyboard (which has a mic button on Google TV)
 */
@Composable
private fun TvSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.titleLarge.copy(color = Color.White),
        cursorBrush = SolidColor(Color.White),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            showKeyboardOnFocus = false,
        ),
        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
        modifier = modifier
            .onFocusChanged { focused = it.isFocused }
            .onPreviewKeyEvent { event ->
                val isOk = event.key == Key.DirectionCenter || event.key == Key.Enter
                if (isOk && event.type == KeyEventType.KeyUp) {
                    keyboard?.show()
                    true
                } else false
            }
            .background(TvColors.Surface, RoundedCornerShape(12.dp))
            .border(
                BorderStroke(2.dp, if (focused) Color.White else Color.Transparent),
                RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        decorationBox = { inner ->
            Box {
                if (value.isEmpty()) {
                    Text("Search movies and series", color = TvColors.Muted, style = MaterialTheme.typography.titleLarge)
                }
                inner()
            }
        },
    )
}



@Composable
private fun TvMessage(text: String) {
    Text(text, color = TvColors.Muted, style = MaterialTheme.typography.bodyLarge)
}