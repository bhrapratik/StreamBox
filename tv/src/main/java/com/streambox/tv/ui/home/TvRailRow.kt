package com.streambox.tv.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.streambox.core.model.Content
import com.streambox.core.model.Rail
import com.streambox.core.model.RailStyle
import com.streambox.tv.ui.components.TvLandscapeCard
import com.streambox.tv.ui.components.TvLogoCard
import com.streambox.tv.ui.components.TvPosterCard

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TvRailRow(
    rail: Rail,
    listState: LazyListState? = null,                     // hoisted → survives the row being disposed
    onItemClick: (Content) -> Unit,
    onItemFocused: (Content) -> Unit,
    modifier: Modifier = Modifier,
    itemModifier: (index: Int, item: Content) -> Modifier = { _, _ -> Modifier },
) {
    Column(modifier) {
        Text(
            text = rail.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 24.dp),
        )
        LazyRow(
            state = listState ?: rememberLazyListState(),
            modifier = Modifier.focusRestorer(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            itemsIndexed(rail.items, key = { _, item -> item.id }) { index, item ->

                val cardModifier = itemModifier(index, item).then(
                    // Last card: → goes nowhere instead of jumping into another row
                    if (index == rail.items.lastIndex) {
                        Modifier.focusProperties { right = FocusRequester.Cancel }
                    } else Modifier
                )
                when (rail.style) {
                    RailStyle.Poster -> TvPosterCard(item, { onItemClick(item) }, { onItemFocused(item) }, cardModifier)
                    RailStyle.Landscape -> TvLandscapeCard(item, { onItemClick(item) }, { onItemFocused(item) }, cardModifier)
                    RailStyle.Logo -> TvLogoCard(item, { onItemClick(item) }, { onItemFocused(item) }, cardModifier)
                }
            }
        }
    }
}