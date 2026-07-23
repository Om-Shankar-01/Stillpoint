package com.example.stillpoint.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.stillpoint.R
import com.example.stillpoint.data.AppTheme
import com.example.stillpoint.ui.homescreen.EditNameDialog
import com.example.stillpoint.ui.readerscreen.ReaderSettingsDialog
import com.example.stillpoint.ui.theme.StillpointTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
    val readerSettings by viewModel.readerSettings.collectAsStateWithLifecycle()
    val isEditNameDialogVisible by viewModel.isEditNameDialogVisible.collectAsStateWithLifecycle()
    val isReaderSettingsDialogVisible by viewModel.isReaderSettingsDialogVisible.collectAsStateWithLifecycle()

    if (isEditNameDialogVisible) {
        EditNameDialog(
            initialName = userName,
            onDismiss = { viewModel.onDismissEditNameDialog() },
            onSave = { viewModel.updateUserName(it) }
        )
    }

    if (isReaderSettingsDialogVisible) {
        ReaderSettingsDialog(
            currentSettings = readerSettings,
            onDismiss = { viewModel.onDismissReaderSettingsDialog() },
            onFontSizeChange = { viewModel.updateFontSize(it) },
            onFontTypeChange = { viewModel.updateFontType(it) },
            onThemeChange = { viewModel.updateReaderTheme(it) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(painter = painterResource(R.drawable.icon_arrow_back), contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSectionTitle("General")
            SettingsClickableItem(
                title = "Your name",
                subtitle = userName,
                onClick = { viewModel.onShowEditNameDialog() }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionTitle("App Theme")
            AppThemeSelector(
                selectedTheme = appTheme,
                onThemeSelected = { viewModel.updateAppTheme(it) }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            SettingsSectionTitle("Reader Appearance")
            SettingsClickableItem(
                title = "Typography & Colors",
                subtitle = "Change font size, type and reader theme",
                onClick = { viewModel.onShowReaderSettingsDialog() }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppThemeSelector(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    modifier: Modifier = Modifier,
) {
    ButtonGroup(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 8.dp),
        overflowIndicator = { Text("...") },
        expandedRatio = 0f
    ) {
        AppTheme.entries.forEach {
            val theme = it
            val isSelected = theme == selectedTheme

            val icon = when(theme.ordinal) {
                0 -> R.drawable.rounded_light_mode
                1 -> R.drawable.rounded_dark_mode
                else -> R.drawable.rounded_mobile
            }

            this.toggleableItem(
                checked = isSelected,
                label = theme.name,
                onCheckedChange = {
                    onThemeSelected(theme)
                },
                icon = {
                    if (isSelected) {
                        Icon(painter = painterResource(icon), contentDescription = null)
                    } else null
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppThemeSelectorPreview() {
    StillpointTheme {
        Surface {
            AppThemeSelector(
                selectedTheme = AppTheme.SYSTEM,
                onThemeSelected = {}
            )
        }
    }
}
