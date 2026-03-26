package com.example.stillpoint.ui.readerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@ExperimentalMaterial3ExpressiveApi()
@Composable
fun ReaderControlsToolbar(
    navController: NavController,
    onSettingsClick: () -> Unit,
    onBrowserClick: () -> Unit,
    scrollBehavior: FloatingToolbarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    HorizontalFloatingToolbar(
        expanded = expanded,
        scrollBehavior = scrollBehavior,
        modifier = modifier
    ) {
        IconButtonWithTooltipBox(
            onClick = { navController.navigateUp() },
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            descriptor = "Back",
            tint = MaterialTheme.colorScheme.onBackground
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                .align(Alignment.CenterVertically)
        )

        IconButtonWithTooltipBox(
            onClick = onSettingsClick,
            icon = Icons.Default.Settings,
            descriptor = "Appearance",
            tint = MaterialTheme.colorScheme.onBackground
        )

        IconButtonWithTooltipBox(
            onClick = onBrowserClick,
            icon = Icons.Default.Language,
            descriptor = "Open in Browser",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconButtonWithTooltipBox(
    onClick: () -> Unit,
    icon: ImageVector? = null,
    descriptor: String,
    tint: Color = LocalContentColor.current,
    modifier: Modifier = Modifier,
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above,
        ),
        state = rememberTooltipState(),
        tooltip = {
            PlainTooltip {
                Text(descriptor)
            }
        },
    ) {
        IconButton(onClick = onClick) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = descriptor, tint = tint)
            }
        }
    }
}
