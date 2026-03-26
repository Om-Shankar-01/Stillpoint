package com.example.stillpoint.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.stillpoint.ui.theme.StillpointTheme

@Composable
fun AppDrawer(
    currentDestination: NavDestination?,
    onNavigateToQueue: () -> Unit,
    onNavigateToArchive: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier.width(280.dp)
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Stillpoint",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.displayMedium,
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))

        /*** Home Item ***/
        NavigationDrawerItem(
            label = { Text("Home", style = MaterialTheme.typography.bodyLarge) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home Screen") },
            selected = currentDestination?.hasRoute<Queue>() == true,
            onClick = onNavigateToQueue,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        /*** Archive Item ***/
        NavigationDrawerItem(
            label = { Text("Archive", style = MaterialTheme.typography.bodyLarge) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Archive,
                    contentDescription = "Archive Screen"
                )
            },
            selected = currentDestination?.hasRoute<Archive>() == true,
            onClick = onNavigateToArchive,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Preview
@Composable
fun AppDrawerPreview() {
    StillpointTheme {
        AppDrawer(
            currentDestination = null,
            onNavigateToQueue = {},
            onNavigateToArchive = {}
        )
    }
}