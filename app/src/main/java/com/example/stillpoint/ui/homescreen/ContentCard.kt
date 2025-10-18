package com.example.stillpoint.ui.homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.stillpoint.data.local.ContentItem
import com.example.stillpoint.data.local.ContentType
import com.example.stillpoint.ui.theme.StillpointTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ContentCard(
    item: ContentItem,
    isSelected: Boolean,
    isStart: Boolean,
    isEnd: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = if (!isSelected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = if (!isSelected) RoundedCornerShape(
            topStart = if (isStart) 16.dp else 4.dp,
            topEnd = if (isStart) 16.dp else 4.dp,
            bottomStart = if (isEnd) 16.dp else 4.dp,
            bottomEnd = if (isEnd) 16.dp else 4.dp,
        ) else {
            RoundedCornerShape(16.dp)
        },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(size = 8.dp))
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = "Article thumbnail",
                    contentScale = ContentScale.Crop,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                    error = ColorPainter(MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.sourceName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.height(IntrinsicSize.Min)
                ) {
                    val iconInfo = if (item.type == ContentType.ARTICLE) {
                        Pair(Icons.Default.Web, "Article")
                    } else {
                        Pair(Icons.Default.OndemandVideo, "Video")
                    }
                    Icon(
                        imageVector = iconInfo.first,
                        contentDescription = iconInfo.second,
                    )
                    Text(
                        text = "${item.estimatedTimeMinutes} min",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

        }
    }
}

@Preview
@Composable
fun ContentCardPreview() {
    val item = ContentItem(
        url = "https://example.com",
        title = "The Art of Doing Nothing: How to Be More Productive by Taking a Break",
        description = "A deep dive into the benefits of resting.",
        imageUrl = "https://picsum.photos/seed/picsum/200/300",
        sourceName = "verylongsourcename.thatshouldbeellipsized.com",
        type = ContentType.ARTICLE,
        estimatedTimeMinutes = 15
    )
    StillpointTheme {
        Surface {
            Box(modifier = Modifier.padding(8.dp)) {
                ContentCard(item = item, isSelected = false, isStart = false, isEnd = false)
            }
        }
    }
}

@Preview
@Composable
fun ContentCardSelectedPreview() {
    val item = ContentItem(
        url = "https://example.com",
        title = "The Art of Doing Nothing: How to Be More Productive by Taking a Break",
        description = "A deep dive into the benefits of resting.",
        imageUrl = "https://picsum.photos/seed/picsum/200/300",
        sourceName = "verylongsourcename.thatshouldbeellipsized.com",
        type = ContentType.ARTICLE,
        estimatedTimeMinutes = 15
    )
    StillpointTheme {
        Surface {
            Box(modifier = Modifier.padding(8.dp)) {
                ContentCard(item = item, isSelected = true, isStart = false, isEnd = false)
            }
        }
    }
}