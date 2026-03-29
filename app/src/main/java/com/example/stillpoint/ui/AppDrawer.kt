package com.example.stillpoint.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import com.example.stillpoint.R
import com.example.stillpoint.ui.theme.StillpointTheme

@Composable
fun AppDrawer(
    currentDestination: NavDestination?,
    onNavigateToQueue: () -> Unit,
    onNavigateToArchive: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(
        modifier = modifier.width(280.dp)
    ) {
        Column(modifier = Modifier.fillMaxHeight()) {
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
                icon = {
                    val homeIcon =
                        if (currentDestination?.hasRoute<Queue>() == true) R.drawable.icon_home_filled
                        else R.drawable.icon_home
                    Icon(
                        painter = painterResource(homeIcon),
                        contentDescription = "Home Screen"
                    )
                },
                selected = currentDestination?.hasRoute<Queue>() == true,
                onClick = onNavigateToQueue,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            /*** Archive Item ***/
            NavigationDrawerItem(
                label = { Text("Archive", style = MaterialTheme.typography.bodyLarge) },
                icon = {
                    val archiveIcon =
                        if (currentDestination?.hasRoute<Archive>() == true) R.drawable.icon_archive_filled
                        else R.drawable.icon_archive
                    Icon(
                        painter = painterResource(archiveIcon),
                        contentDescription = "Archive Screen"
                    )
                },
                selected = currentDestination?.hasRoute<Archive>() == true,
                onClick = onNavigateToArchive,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            /*** Settings Item ***/
            NavigationDrawerItem(
                label = { Text("Settings", style = MaterialTheme.typography.bodyLarge) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.icon_settings_filled),
                        contentDescription = "Settings Screen"
                    )
                },
                selected = currentDestination?.hasRoute<Settings>() == true,
                onClick = onNavigateToSettings,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun AppDrawerPreview() {
    StillpointTheme {
        AppDrawer(
            currentDestination = null,
            onNavigateToQueue = {},
            onNavigateToArchive = {},
            onNavigateToSettings = {}
        )
    }
}
