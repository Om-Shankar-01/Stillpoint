package com.example.stillpoint.ui.archivescreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.stillpoint.data.local.ContentItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreenAppBar (
    navController: NavController,
    deleteItemsAction: () -> Unit,
    selectionItems: Set<ContentItem>,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    val hasSelection = selectionItems.isNotEmpty()
    val topBarText = if (hasSelection) {
        "Selected ${selectionItems.size} items"
    } else {
        "Archive"
    }


    TopAppBar(
        title = { Text(topBarText) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            if (hasSelection) {
                IconButton(onClick = { deleteItemsAction() }) {
                    Icon(imageVector = Icons.Outlined.DeleteOutline, contentDescription = "Delete items")
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}