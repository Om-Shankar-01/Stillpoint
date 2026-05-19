package com.example.stillpoint.ui.readerscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.stillpoint.R
import com.example.stillpoint.ui.components.IconButtonWithTooltipBox
import com.example.stillpoint.ui.theme.StillpointTheme
import com.example.stillpoint.utils.TtsState

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReaderControlsToolbar(
    navController: NavController,
    onSettingsClick: () -> Unit,
    onBrowserClick: () -> Unit,
    onPlaybackClick: () -> Unit,
    ttsState: TtsState,
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
            icon = R.drawable.icon_arrow_back,
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

        val playbackIcon = if (ttsState == TtsState.PLAYING) {
            R.drawable.icon_pause_filled
        } else {
            R.drawable.icon_play_arrow_filled
        }

        val playbackDescriptor = if (ttsState == TtsState.PLAYING) "Stop Listening" else "Listen"

        IconButtonWithTooltipBox(
            onClick = onPlaybackClick,
            icon = playbackIcon,
            descriptor = playbackDescriptor,

            // Gray out the icon if TtsState is INITIALIZING or ERROR
            tint = if (ttsState == TtsState.INITIALIZING) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.onBackground
        )

        IconButtonWithTooltipBox(
            onClick = onSettingsClick,
            icon = R.drawable.icon_format_size,
            descriptor = "Appearance",
            tint = MaterialTheme.colorScheme.onBackground
        )

        IconButtonWithTooltipBox(
            onClick = onBrowserClick,
            icon = R.drawable.icon_open_in_browser,
            descriptor = "Open in Browser",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun ReaderControlsToolbarPreview() {
    StillpointTheme {
        ReaderControlsToolbar(
            navController = rememberNavController(),
            onSettingsClick = {},
            onBrowserClick = {},
            onPlaybackClick = {},
            ttsState = TtsState.IDLE,
            scrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
                exitDirection = FloatingToolbarExitDirection.Bottom
            )
        )
    }
}
