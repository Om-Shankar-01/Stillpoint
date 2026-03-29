package com.example.stillpoint.ui.archivescreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.stillpoint.R
import com.example.stillpoint.data.local.ContentItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreenAppBar (
    navController: NavController,
    deleteItemsAction: () -> Unit,
    unarchiveItemsAction: () -> Unit,
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
                Icon(painter = painterResource(R.drawable.icon_arrow_back), contentDescription = "Back")
            }
        },
        actions = {
            if (hasSelection) {
                IconButton(onClick = { unarchiveItemsAction() }) {
                    Icon(painter = painterResource(R.drawable.icon_unarchive), contentDescription = "Unarchive items")
                }
                IconButton(onClick = { deleteItemsAction() }) {
                    Icon(painter = painterResource(R.drawable.icon_delete), contentDescription = "Delete items")
                }
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}