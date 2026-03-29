package com.example.stillpoint.ui.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.stillpoint.R
import com.example.stillpoint.data.local.TimeFilter

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeSection(
    userName: String,
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    onNameClick: () -> Unit,
    scrollProgress: Float,
    modifier: Modifier = Modifier
) {
    val topPadding = 28.dp - (20.dp * scrollProgress)
    val titleScale = 1f - (0.2f * scrollProgress)
    val subTextAlpha = 1f - scrollProgress
    val subTextHeight = 12.dp - (12.dp * scrollProgress)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(Modifier.size(topPadding))
        Text(
            text = "Hello, $userName",
            style = MaterialTheme.typography.displayMedium,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable { onNameClick() }
                .graphicsLayer {
                    scaleX = titleScale
                    scaleY = titleScale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
        )
        Spacer(modifier = Modifier.size(8.dp * subTextAlpha))
        Text(
            text = "How much time do you have?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.graphicsLayer{
                alpha = subTextAlpha
            }

        )
        Spacer(modifier = Modifier.height(subTextHeight))

        // Horizontal scrollable list of filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TimeFilter.entries.toTypedArray(), key = { it.name }) { filter ->
                FilterChip(
                    selected = (filter == selectedFilter),
                    onClick = { onFilterSelected(filter) },
                    label = {
                        Text(
                            filter.displayText,
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingIcon = if (filter == selectedFilter) {
                        {
                            Icon(
                                painter = painterResource(R.drawable.icon_check),
                                contentDescription = "Done icon",
                                modifier = Modifier.size(FilterChipDefaults.IconSize)
                            )
                        }
                    } else {
                        null
                    },
                )
            }
        }
    }
}
