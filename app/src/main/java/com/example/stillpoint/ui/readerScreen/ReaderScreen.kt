package com.example.stillpoint.ui.readerScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.stillpoint.data.FontType
import com.example.stillpoint.data.ReaderSettings
import com.example.stillpoint.data.ReaderTheme
import com.example.stillpoint.ui.theme.headingFontFamily
import java.net.URL
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReaderScreen(
    viewModel: ReaderViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val exitAlwaysScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        FloatingToolbarExitDirection.Bottom
    )

    val readerColors = getReaderColors(settings.theme)

    if (uiState.isSettingsVisible) {
        ReaderSettingsDialog(
            currentSettings = settings,
            onDismiss = { viewModel.toggleSettings() },
            onFontSizeChange = { viewModel.updateFontSize(it) },
            onFontTypeChange = { viewModel.updateFontType(it) },
            onThemeChange = { viewModel.updateTheme(it) }
        )
    }

    Scaffold(
        containerColor = readerColors.background,
        modifier = Modifier.nestedScroll(exitAlwaysScrollBehavior)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(color = readerColors.primary)
                    }
                }

                uiState.error != null -> {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("Error: ${uiState.error}", color = Color.Red)
                    }
                }

                uiState.article != null -> {
                    val blocks = remember(uiState.article?.body, uiState.url) {
                        HtmlParser.parse(uiState.article!!.body, uiState.url)
                    }

                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 100.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Article Title as the first item
                        item {
                            val host = remember(uiState.url) {
                                try {
                                    URL(uiState.url).host.replace("www.", "")
                                } catch (e: Exception) {
                                    ""
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp)
                            ) {
                                Text(
                                    text = uiState.article!!.title ?: "Untitled",
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = readerColors.onBackground
                                    )
                                )
                                if (host.isNotEmpty()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = host.uppercase(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = readerColors.primary,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                }
                            }
                        }

                        items(blocks) { block ->
                            RenderBlock(block, settings, readerColors)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }

            // Floating Expressive Toolbar & Progress Indicator
//            ReaderFloatingControls(
//                navController = navController,
//                onSettingsClick = { viewModel.toggleSettings() },
//                onBrowserClick = { viewModel.openInBrowser(context) },
//                listState = listState,
//                colors = readerColors
//            )

            val progress by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val totalItems = layoutInfo.totalItemsCount
                    if (totalItems <= 1) 0f
                    else {
                        val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        (lastVisibleItem.toFloat() / (totalItems - 1).toFloat()).coerceIn(0f, 1f)
                    }
                }
            }

            ReaderControlsToolbar(
                navController = navController,
                onSettingsClick = { viewModel.toggleSettings() },
                onBrowserClick = { viewModel.openInBrowser(context) },
                scrollBehavior = exitAlwaysScrollBehavior,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = -ScreenOffset)
                        .zIndex(1f),
            )

            Spacer(Modifier.height(12.dp))

            // Floating Reading Progress
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(0.6f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun BoxScope.ReaderFloatingControls(
    navController: NavController,
    onSettingsClick: () -> Unit,
    onBrowserClick: () -> Unit,
    listState: LazyListState,
    colors: ReaderColors
) {
    val progress by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems <= 1) 0f
            else {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                (lastVisibleItem.toFloat() / (totalItems - 1).toFloat()).coerceIn(0f, 1f)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = colors.background.copy(alpha = 0.95f),
            tonalElevation = 6.dp,
            shadowElevation = 8.dp,
            border = BorderStroke(
                0.5.dp,
                colors.onBackground.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colors.onBackground)
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(colors.onBackground.copy(alpha = 0.1f))
                )

                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, "Appearance", tint = colors.onBackground)
                }

                IconButton(onClick = onBrowserClick) {
                    Icon(Icons.Default.Language, "Open in Browser", tint = colors.onBackground)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Floating Reading Progress
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .width(100.dp)
                .height(4.dp)
                .clip(CircleShape),
            color = colors.primary,
            trackColor = colors.onBackground.copy(alpha = 0.1f)
        )
    }
}

@Composable
fun RenderBlock(
    block: HtmlBlock,
    settings: ReaderSettings,
    colors: ReaderColors
) {
    val fontFamily = when (settings.fontType) {
        FontType.SANS_SERIF -> FontFamily.SansSerif
        FontType.SERIF -> FontFamily.Serif
        FontType.MONOSPACE -> FontFamily.Monospace
    }

    when (block) {
        is HtmlBlock.Header -> {
            val style = when (block.level) {
                1 -> MaterialTheme.typography.headlineLarge
                2 -> MaterialTheme.typography.headlineMedium
                3 -> MaterialTheme.typography.headlineSmall
                else -> MaterialTheme.typography.titleLarge
            }
            Text(
                text = block.text,
                style = style.copy(
                    fontFamily = headingFontFamily,
                    color = colors.onBackground
                )
            )
        }

        is HtmlBlock.Paragraph -> {
            HtmlText(block.html, settings, colors, fontFamily)
        }

        is HtmlBlock.Image -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = block.url,
                    contentDescription = block.caption,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.onBackground.copy(alpha = 0.05f)),
                    contentScale = ContentScale.FillWidth
                )
                block.caption?.let {
                    if (it.isNotBlank()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic,
                                color = colors.onBackground.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        is HtmlBlock.BlockQuote -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(vertical = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .background(colors.primary)
                        .fillMaxHeight()
                )
                Spacer(Modifier.width(20.dp))
                HtmlText(
                    html = block.html,
                    settings = settings,
                    colors = colors,
                    fontFamily = headingFontFamily, // Use serif for quotes
                    fontStyle = FontStyle.Italic
                )
            }
        }

        is HtmlBlock.ListItem -> {
            Row(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = if (block.isOrdered) "${block.index}." else "•",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = colors.primary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.width(32.dp)
                )
                HtmlText(block.html, settings, colors, fontFamily)
            }
        }
    }
}

@Composable
fun HtmlText(
    html: String,
    settings: ReaderSettings,
    colors: ReaderColors,
    fontFamily: FontFamily,
    fontStyle: FontStyle = FontStyle.Normal
) {
    val baseStyle = MaterialTheme.typography.bodyLarge

    Text(
        text = AnnotatedString.fromHtml(
            html,
            linkStyles = TextLinkStyles(
                style = SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = colors.primary
                )
            )
        ),
        style = baseStyle.copy(
            fontSize = settings.fontSize.sp,
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            lineHeight = (settings.fontSize * 1.6).sp, // Maintain breathable line height
            color = colors.onBackground
        )
    )
}

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
                        AssistChip(
                            onClick = { onFontTypeChange(type) },
                            label = {
                                Text(
                                    type.name.lowercase()
                                        .replace('_', ' ')
                                        .replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.ROOT
                                            ) else it.toString()
                                        }, overflow = TextOverflow.Ellipsis)
                            },
                            border = if (currentSettings.fontType == type) null else AssistChipDefaults.assistChipBorder(
                                enabled = true
                            )
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

@Composable
fun ThemeColorButton(theme: ReaderTheme, isSelected: Boolean, onClick: () -> Unit) {
    val colors = getReaderColors(theme)
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            color = colors.background,
            border = if (isSelected) BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary
            ) else null,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "Aa",
                    color = colors.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            theme.name,
            textAlign = TextAlign.Center,
        )
    }
}

data class ReaderColors(val background: Color, val onBackground: Color, val primary: Color)

@Composable
fun getReaderColors(theme: ReaderTheme): ReaderColors {
    val isSystemDark = isSystemInDarkTheme()
    return when (theme) {
        ReaderTheme.LIGHT -> ReaderColors(Color(0xFFFFFFFF), Color(0xFF1A1A1A), Color(0xFF2196F3))
        ReaderTheme.DARK -> ReaderColors(Color(0xFF121212), Color(0xFFE0E0E0), Color(0xFF90CAF9))
        ReaderTheme.SEPIA -> ReaderColors(Color(0xFFF4ECD8), Color(0xFF5B4636), Color(0xFF8B4513))
        ReaderTheme.SYSTEM -> if (isSystemDark) {
            ReaderColors(Color(0xFF121212), Color(0xFFE0E0E0), Color(0xFF90CAF9))
        } else {
            ReaderColors(Color(0xFFFFFFFF), Color(0xFF1A1A1A), Color(0xFF2196F3))
        }
    }
}
