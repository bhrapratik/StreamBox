package com.streambox.uimobile.ui.home
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.streambox.core.model.Content
import com.streambox.core.model.Rail
import com.streambox.core.model.RailStyle
import com.streambox.uimobile.ui.components.LandscapeCard
import com.streambox.uimobile.ui.components.LogoCard
import com.streambox.uimobile.ui.components.PosterCard
import com.streambox.uimobile.ui.theme.StreamColors


@Composable
fun RailRow(rail: Rail, onItemClick: (Content) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.padding(vertical = 12.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = rail.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text("Show more", style = MaterialTheme.typography.bodyMedium, color = StreamColors.Muted)
        }
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(rail.items, key = { it.id }) { item ->
                when (rail.style) {
                    RailStyle.Poster -> PosterCard(item, onClick = { onItemClick(item) })
                    RailStyle.Landscape -> LandscapeCard(item, onClick = { onItemClick(item) })
                    RailStyle.Logo -> LogoCard(item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}