package com.example.stillpoint.ui.archivescreen

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stillpoint.data.local.ContentItem
import com.example.stillpoint.ui.QueueViewModel
import com.example.stillpoint.ui.Reader
import com.example.stillpoint.ui.homescreen.ContentCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    navController: NavController,
    viewModel: QueueViewModel = hiltViewModel()
) {
    val archivedItems by viewModel.archivedItems.collectAsStateWithLifecycle()
    var selectionItems by rememberSaveable { mutableStateOf(setOf<ContentItem>()) }

    var inSelection by rememberSaveable { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ArchiveScreenAppBar(
                navController = navController,
                deleteItemsAction = { viewModel.deleteMultipleItems(selectionItems.toList()) },
                selectionItems = selectionItems,
                scrollBehavior = scrollBehavior,
            )
        }
    ) { paddingValues ->
        if (archivedItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("You haven't archived any items yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(archivedItems, key = { it.id }) { item ->
                    val isItemSelected = selectionItems.contains(item)
                    ContentCard(
                        item = item,
                        isSelected = isItemSelected,
                        isStart = archivedItems.indexOf(item) == 0,
                        isEnd = archivedItems.indexOf(item) == archivedItems.lastIndex,
                        modifier = Modifier.combinedClickable(
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                if (!inSelection) navController.navigate(Reader(url = item.url))
                                else {
                                    if (isItemSelected)
                                        selectionItems -= item
                                    else
                                        selectionItems += item

                                    if (selectionItems.isEmpty())
                                        inSelection = false
                                }
                            },
                            onLongClick = {
                                if (isItemSelected) {
                                    selectionItems -= item
                                    if (selectionItems.isEmpty())
                                        inSelection = false
                                } else {
                                    if (!inSelection)
                                        inSelection = true
                                    selectionItems += item
                                }
                            }
                        )
                    )
                }
            }
        }
    }
}