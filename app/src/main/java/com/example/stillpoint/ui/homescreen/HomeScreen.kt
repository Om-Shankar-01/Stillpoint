package com.example.stillpoint.ui.homescreen

import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stillpoint.R
import com.example.stillpoint.ui.Reader
import com.example.stillpoint.ui.UiEvent

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    onOpenDrawer: () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
) {
    val items by viewModel.filteredItems.collectAsStateWithLifecycle()
    val selectionItems by viewModel.selectedItems.collectAsStateWithLifecycle()
    val inSelection by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val itemToDelete by viewModel.itemToDelete.collectAsStateWithLifecycle()

    // Dialog state variable
    val isAddDialogVisible by viewModel.isAddDialogVisible.collectAsStateWithLifecycle()
    val isEditNameDialogVisible by viewModel.isEditNameDialogVisible.collectAsStateWithLifecycle()
    val isMultiDeleteDialogVisible by viewModel.isMultiDeleteDialogVisible.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val listState = rememberLazyListState()
    val scrollProgress by remember {
        derivedStateOf {
            if (listState.firstVisibleItemIndex > 0) {
                1f
            } else {
                (listState.firstVisibleItemScrollOffset / 300f).coerceIn(0f, 1f)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is UiEvent.OpenVideo -> {
                    val intent = Intent(Intent.ACTION_VIEW, event.url.toUri())
                    context.startActivity(intent)
                }
                is UiEvent.NavigateToReader -> {
                    navController.navigate(Reader(url = event.url))
                }
            }
        }
    }

    if (isAddDialogVisible) {
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

    if (isMultiDeleteDialogVisible) {
        MultiDeleteDialog(
            onDismiss = { viewModel.onDismissMultiDeleteDialog() },
            onConfirm = { viewModel.deleteSelectedItems() },
            selectionSize = selectionItems.size
        )
    }

    if (itemToDelete != null) {
        DeleteDialog(
            onDismiss = { viewModel.cancelDeletion() },
            onConfirm = { viewModel.confirmSingleDeletion() }
        )
    }

    Scaffold(
        topBar = {
            if (inSelection) {
                TopAppBar(
                    title = { Text("${selectionItems.size} selected") },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.clearSelection() }) {
                            Icon(
                                painter = painterResource(R.drawable.icon_close),
                                contentDescription = "Clear Selection"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.archiveSelectedItems() }) {
                            Icon(
                                painter = painterResource(R.drawable.icon_archive),
                                contentDescription = "Archive Selected"
                            )
                        }
                        IconButton(onClick = { viewModel.onShowMultiDeleteDialog() }) {
                            Icon(
                                painter = painterResource(R.drawable.icon_delete),
                                contentDescription = "Delete Selected"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            } else {
                TopAppBar(title = { Text("") }, navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            painter = painterResource(R.drawable.icon_menu),
                            contentDescription = "Open Navigation Drawer",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                })
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onShowAddDialog() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_add),
                    contentDescription = "Add link manually",
                )
                Spacer(Modifier.width(4.dp))
                Text("Add", style = MaterialTheme.typography.bodyLarge)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WelcomeSection(
                userName = userName,
                selectedFilter = selectedFilter,
                onFilterSelected = { filter -> viewModel.selectFilter(filter) },
                onNameClick = { viewModel.onShowEditNameDialog() },
                scrollProgress = scrollProgress,
            )

            /* An earlier implementation of the Archive Button
            Probably never to be used again. \(-_-)/
            Box {
                OutlinedButton(
                    onClick = { navController.navigate(Archive) },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_archive),
                        contentDescription = "Archive"
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Archive", fontWeight = FontWeight.Bold)
                }
            }

            */

            if (items.isEmpty()) {
                EmptyQueueView()
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        val dismissState = rememberSwipeToDismissBoxState()

                        LaunchedEffect(dismissState.currentValue) {
                            when (dismissState.currentValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    viewModel.onSwipeToDelete(item)
                                }

                                SwipeToDismissBoxValue.StartToEnd -> viewModel.archiveItem(item)
                                SwipeToDismissBoxValue.Settled -> { /* Eat 5 Star */
                                }
                            }
                        }

                        /* This Launched Effect block resets the swiped Delete action and returns
                           the dismissState (of the Card) back to its original position */
                        LaunchedEffect(itemToDelete) {
                            if (itemToDelete == null && dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                dismissState.reset()
                            }
                        }

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val (color, iconPainter, alignment) = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        val painter = painterResource(R.drawable.icon_archive)
                                        Triple(
                                            Color(0xFF2A712E),
                                            painter,
                                            Alignment.CenterStart,
                                        )
                                    }

                                    SwipeToDismissBoxValue.EndToStart -> Triple(
                                        Color(0xFFAC2828),
                                        painterResource(R.drawable.icon_delete),
                                        Alignment.CenterEnd,
                                    )

                                    else -> Triple(
                                        Color.Transparent,
                                        null,
                                        Alignment.CenterEnd
                                    )
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
                                    if (iconPainter != null) {
                                        Icon(
                                            painter = iconPainter,
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
                                        viewModel.onItemClick(item)
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
            textAlign = TextAlign.Center
        )
    }
}
