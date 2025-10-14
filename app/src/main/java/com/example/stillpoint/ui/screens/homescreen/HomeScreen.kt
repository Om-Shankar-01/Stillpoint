package com.example.stillpoint.ui.screens.homescreen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.stillpoint.data.local.ContentItem
import com.example.stillpoint.data.local.ContentType
import com.example.stillpoint.ui.Archive
import com.example.stillpoint.ui.QueueViewModel
import com.example.stillpoint.ui.Reader
import com.example.stillpoint.ui.TimeFilter
import com.example.stillpoint.ui.UiEvent
import com.example.stillpoint.ui.theme.StillpointTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: QueueViewModel = hiltViewModel<QueueViewModel>()
) {
    val items by viewModel.filteredItems.collectAsStateWithLifecycle()
    // This will eventually come from a ViewModel
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()

    // Dialog state variable
    val isDialogVisible by viewModel.isAddDialogVisible.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                }
            }
        }
    }

    if (isDialogVisible) {
        AddLinkDialog(
            onDismiss = { viewModel.onDismissAddDialog() },
            onSave = { url -> viewModel.saveManuallyAddedLink(url) }
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { viewModel.onShowAddDialog() }) {
                Icon(Icons.Filled.Add, contentDescription = "Add link manually")
                Spacer(Modifier.width(4.dp))
                Text("Add")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val (cornerDp, altCornerDp) = Pair(4.dp, 16.dp)
            WelcomeSection(
                userName = "Om", /* TODO: Hardcoded for now */
                selectedFilter = selectedFilter,
                onFilterSelected = { filter -> viewModel.selectFilter(filter) },
                navController = navController
            )
            if (items.isEmpty()) {
                EmptyQueueView()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(dismissState.currentValue) {
                            when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart -> viewModel.deleteItem(item)
                                SwipeToDismissBoxValue.StartToEnd -> viewModel.archiveItem(item)
                                SwipeToDismissBoxValue.Settled -> { /* Eat 5 Star */
                                }
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val (color, icon, alignment) = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> Triple(
                                        Color(0xFF2A712E),
                                        Icons.Default.Archive,
                                        Alignment.CenterStart
                                    )

                                    SwipeToDismissBoxValue.EndToStart -> Triple(
                                        Color(0xFF750E0E),
                                        Icons.Default.Delete,
                                        Alignment.CenterEnd
                                    )

                                    else -> Triple(Color.Transparent, null, Alignment.CenterEnd)
                                }
                                val scale by animateFloatAsState(
                                    targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.8f else 1.2f,
                                    label = "icon scale"
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    if (icon != null) {
                                        Icon(
                                            icon,
                                            contentDescription = "Action Icon",
                                            modifier = Modifier.scale(scale),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        ) {
                            ContentCard(
                                item = item,
                                onClick = { navController.navigate(Reader(url = item.url)) },
                                isStart = items.indexOf(item) == 0,
                                isEnd = items.indexOf(item) == items.lastIndex
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeSection(
    userName: String,
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Spacer(Modifier.size(80.dp))
        Text(
            text = "Hello, $userName 👋",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.W600
        )
        IconButton(onClick = { navController.navigate(Archive) }) {
            Icon(
                imageVector = Icons.Default.Inventory2,
                contentDescription = "Open Archive"
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "How much time do you have?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Horizontal scrollable list of filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TimeFilter.entries.toTypedArray(), key = { it.name }) { filter ->
                FilterChip(
                    selected = (filter == selectedFilter),
                    onClick = { onFilterSelected(filter) },
                    label = { Text(filter.displayText) },
                    leadingIcon = if (filter == selectedFilter) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Done,
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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ContentCard(item: ContentItem, onClick: () -> Unit, isStart: Boolean, isEnd: Boolean) {
    Card(
        shape = RoundedCornerShape(
            topStart = if (isStart) 16.dp else 4.dp,
            topEnd = if (isStart) 16.dp else 4.dp,
            bottomStart = if (isEnd) 16.dp else 4.dp,
            bottomEnd = if (isEnd) 16.dp else 4.dp
        ),
        modifier = Modifier
            .height(96.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(size = 16.dp))
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
                        .clip(RoundedCornerShape(size = 16.dp))
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
                    maxLines = 1,
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
                val emoji = if (item.type == ContentType.ARTICLE) "📖" else "▶️"
                Text(
                    text = "$emoji ${item.estimatedTimeMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

        }
    }
}

@Composable
fun EmptyQueueView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Your queue is ready.\nShare an article or video to begin!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
                ContentCard(item = item, onClick = {}, isStart = false, isEnd = false)
            }
        }
    }
}
