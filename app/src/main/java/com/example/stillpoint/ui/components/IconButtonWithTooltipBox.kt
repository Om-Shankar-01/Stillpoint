package com.example.stillpoint.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconButtonWithTooltipBox(
    onClick: () -> Unit,
    descriptor: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int?,
    tint: Color = LocalContentColor.current,
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
                Icon(painter = painterResource(icon), contentDescription = descriptor, tint = tint)
            }
        }
    }
}