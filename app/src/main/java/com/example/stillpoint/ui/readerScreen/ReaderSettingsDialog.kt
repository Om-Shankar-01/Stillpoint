package com.example.stillpoint.ui.readerScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.stillpoint.data.FontType
import com.example.stillpoint.data.ReaderSettings
import com.example.stillpoint.data.ReaderTheme
import java.util.Locale

@Composable
fun ReaderSettingsDialog(
    currentSettings: ReaderSettings,
    onDismiss: () -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onFontTypeChange: (FontType) -> Unit,
    onThemeChange: (ReaderTheme) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Appearance", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text("Font Size", style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = currentSettings.fontSize.toFloat(),
                    onValueChange = { onFontSizeChange(it.toInt()) },
                    valueRange = 12f..32f,
                    steps = 20
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Typography", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FontType.entries.forEach { type ->
                        FilterChip(
                            selected = currentSettings.fontType == type,
                            onClick = { onFontTypeChange(type) },
                            label = {
                                Text(
                                    type.name.lowercase()
                                        .replace('_', ' ')
                                        .replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.ROOT
                                            ) else it.toString()
                                        }, overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = if (currentSettings.fontType == type) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Reader Theme", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReaderTheme.entries.forEach { theme ->
                        ThemeColorButton(
                            theme = theme,
                            isSelected = currentSettings.theme == theme,
                            onClick = { onThemeChange(theme) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}