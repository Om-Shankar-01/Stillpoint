package com.example.stillpoint.ui.homescreen

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stillpoint.data.local.TimeFilter
import com.example.stillpoint.ui.Archive
import com.example.stillpoint.ui.QueueViewModel
import com.example.stillpoint.ui.Reader
import com.example.stillpoint.ui.UiEvent

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: QueueViewModel = hiltViewModel<QueueViewModel>()
) {
    val items by viewModel.filteredItems.collectAsStateWithLifecycle()
    val selectionItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val inSelection by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()

    // Dialog state variable
    val isDialogVisible by viewModel.isAddDialogVisible.collectAsStateWithLifecycle()
    val isEditNameDialogVisible by viewModel.isEditNameDialogVisible.collectAsStateWithLifecycle()

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

    if (isEditNameDialogVisible) {
        EditNameDialog(
            initialName = userName,
            onDismiss = { viewModel.onDismissEditNameDialog() },
            onSave = { newName -> viewModel.updateUserName(newName) }
        )
    }

    Scaffold(
        topBar = {
            if (inSelection) {
                TopAppBar(
                    title = { Text("${selectionItems.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.archiveSelectedItems() }) {
                            Icon(Icons.Default.Archive, contentDescription = "Archive Selected")
                        }
                        IconButton(onClick = { viewModel.deleteSelectedItems() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Selected")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onShowAddDialog() },
            ) {
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
            Box {
                OutlinedButton (
                    onClick = { navController.navigate(Archive) },
                    modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Outlined.Inventory2, contentDescription = "Archive")
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Archive", fontWeight = FontWeight.Bold)
                }
                WelcomeSection(
                    userName = userName,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { filter -> viewModel.selectFilter(filter) },
                    onNameClick = { viewModel.onShowEditNameDialog() }
                )
            }
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
                                SwipeToDismissBoxValue.Settled -> { /* Eat 5 Star */ }
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
                                        Color(0xFFAC2828),
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
                                isSelected = selectionItems.contains(item),
                                isStart = items.indexOf(item) == 0,
                                isEnd = items.indexOf(item) == items.lastIndex,
                                modifier = Modifier.combinedClickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    onClick = {
                                        if (inSelection) {
                                            viewModel.toggleSelection(item)
                                        } else {
                                            navController.navigate(Reader(url = item.url))
                                        }
                                    },
                                    onLongClick = {
                                        viewModel.toggleSelection(item)
                                    }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeSection(
    userName: String,
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit,
    onNameClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Spacer(Modifier.size(80.dp))
        Text(
            text = "Hello, $userName",
            style = MaterialTheme.typography.displayMedium,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { onNameClick() }
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = "How much time do you have?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
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
                    label = { Text(filter.displayText, style = MaterialTheme.typography.labelLarge) },
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
