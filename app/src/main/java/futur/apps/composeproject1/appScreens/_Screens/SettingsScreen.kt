package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1.quiz.ui.theme.AllPalettes
import futur.apps.composeproject1.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var expandedLang by remember { mutableStateOf(false) }
    var expandedMaxQuestions by remember { mutableStateOf(false) }
    var showPaletteDialog by remember { mutableStateOf(false) }

    val languages = listOf("English", "French", "Arabic")
    val questionOptions = listOf(10, 15, 20)

    if(uiState.isLoading){
        ThemeLoadingScreen()
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ---------------- Appearance ----------------
        item {
            SectionHeader(title = "Appearance")

            SettingCard(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                description = "Switch between light and dark themes"
            ) {
                Switch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.updateDarkMode(it) }
                )
            }

            // Palette Selector
            SettingCard(
                icon = Icons.Default.Palette,
                title = "App Colors",
                description = "Choose a professional color palette"
            ) {
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable { showPaletteDialog = true }
                        .padding(horizontal = 6.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = uiState.selectedPalette,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // ---------------- Language ----------------
        item {
            SettingCard(
                icon = Icons.Default.Language,
                title = "Language",
                description = "Choose app language"
            ) {
                Text(
                    text = uiState.language,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { expandedLang = !expandedLang }
                )
            }

            if (expandedLang) {
                Column(modifier = Modifier.padding(start = 48.dp, top = 4.dp)) {
                    languages.forEach { lang ->
                        Text(
                            text = lang,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedLang = false
                                    viewModel.updateLanguage(lang)
                                }
                                .padding(vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ---------------- General ----------------
        item {
            SectionHeader(title = "General")

            SettingCard(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                description = "Daily quiz reminders"
            ) {
                Switch(
                    checked = true, // Placeholder — add later if you persist notifications
                    onCheckedChange = { /* TODO */ }
                )
            }
        }

        // ---------------- Quiz Configuration ----------------

        item {
            SectionHeader(title = "Quiz Configuration")

            SettingCard(
                icon = Icons.Default.PlayCircle,
                title = "Auto-Next",
                description = "Automatically move to next question"
            ) {
                Switch(
                    checked = uiState.autoNext,
                    onCheckedChange = { viewModel.updateAutoNext(it) }
                )
            }

            SettingCard(
                icon = Icons.Default.Audiotrack,
                title = "Sound Effects",
                description = "Enable quiz sounds"
            ) {
                Switch(
                    checked = uiState.soundEnabled,
                    onCheckedChange = { viewModel.updateSound(it) }
                )
            }

            SettingCard(
                icon = Icons.Default.RecordVoiceOver,
                title = "Text-to-Speech",
                description = "Read questions aloud"
            ) {
                Switch(
                    checked = uiState.ttsEnabled,
                    onCheckedChange = { viewModel.updateTTS(it) }
                )
            }

            SettingCard(
                icon = Icons.Default.Tune,
                title = "Max Questions",
                description = "Questions per quiz session"
            ) {
                Text(
                    text = "${uiState.maxQuestions}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { expandedMaxQuestions = !expandedMaxQuestions }
                )
            }

            if (expandedMaxQuestions) {
                Column(modifier = Modifier.padding(start = 48.dp, top = 4.dp)) {
                    questionOptions.forEach { count ->
                        Text(
                            text = "$count questions",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedMaxQuestions = false
                                    viewModel.updateMaxQuestions(count)
                                }
                                .padding(vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    // ---------------- Palette Dialog ----------------
    if (showPaletteDialog) {
        Dialog(onDismissRequest = { showPaletteDialog = false }) {
            Surface(
                tonalElevation = 8.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Select Color Palette", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(5),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 250.dp)
                    ) {
                        items(AllPalettes) { palette ->
                            val colors = listOf(
                                palette.lightColors.primary,
                                palette.lightColors.secondary,
                                palette.lightColors.tertiary
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                colors.forEach { color ->
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(MaterialTheme.shapes.small)
                                            .background(color)
                                            .border(
                                                width = 2.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .clickable {
                                                viewModel.updatePalette(palette.name)
                                                showPaletteDialog = false
                                            }
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(palette.name, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showPaletteDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp, start = 2.dp)
    )
}

@Composable
private fun SettingCard(
    icon: ImageVector,
    title: String,
    description: String,
    action: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                    Text(
                        description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            action()
        }
    }
}


