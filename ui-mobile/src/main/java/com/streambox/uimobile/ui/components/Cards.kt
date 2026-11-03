package com.streambox.uimobile.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.streambox.core.model.Content
import com.streambox.uimobile.ui.theme.StreamColors


private val CardShape = RoundedCornerShape(6.dp)

@Composable
fun PosterCard(content: Content, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .width(130.dp)
            .aspectRatio(2f / 3f)
            .clip(CardShape)
            .background(StreamColors.Surface)
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = content.posterUrl,
            contentDescription = content.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        content.badge?.let { Badge(it, Modifier.align(Alignment.TopStart)) }
    }
}

@Composable
fun LandscapeCard(content: Content, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.width(220.dp).clickable(onClick = onClick)) {
        AsyncImage(
            model = content.backdropUrl ?: content.posterUrl,
            contentDescription = content.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(CardShape)
                .background(StreamColors.Surface),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = content.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        content.badge?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = StreamColors.Muted, maxLines = 1)
        }
    }
}

@Composable
fun LogoCard(content: Content, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(110.dp)
            .clip(CardShape)
            .background(StreamColors.Surface)
            .clickable(onClick = onClick)
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = content.posterUrl,
            contentDescription = content.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
        )
    }
}

@Composable
private fun Badge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = Color.White,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier
            .background(StreamColors.Primary, RoundedCornerShape(bottomEnd = 6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}