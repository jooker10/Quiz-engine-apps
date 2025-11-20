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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import futur.apps.composeproject1.quiz.ui.theme.AllPalettes
import futur.apps.composeproject1.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Dialog flags
    var showPaletteDialog by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }
    var showQuestionsDialog by remember { mutableStateOf(false) }

    val languages = listOf("English", "French", "Arabic")
    val questionOptions = listOf(10, 15, 20)

    if (uiState.isLoading) {
        ScreenLoadingIndicator()
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp) // ⬅ adds space between sections
    ) {
        // ---------------- Appearance ----------------
        item {
            SectionHeader("Appearance")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) { // ⬅ space between items
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

                SettingCard(
                    icon = Icons.Default.Palette,
                    title = "App Colors",
                    description = "Choose a professional color palette",
                    clickable = { showPaletteDialog = true }
                ) {
                    Text(
                        uiState.selectedPalette,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // ---------------- Language ----------------
        item {
            SectionHeader("Language")

            SettingCard(
                icon = Icons.Default.Language,
                title = "Language",
                description = "Choose app language",
                clickable = { showLangDialog = true }
            ) {
                Text(
                    uiState.language,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp
                )
            }
        }

        // ---------------- General ----------------
        item {
            SectionHeader("General")

            SettingCard(
                icon = Icons.Default.Notifications,
                title = "Quiz Reminders",
                description = "Enable daily reminder notifications"
            ) {
                Switch(
                    checked = uiState.reminderEnabled,
                    onCheckedChange = { viewModel.updateReminderEnabled(it) }
                )
            }

        }

        // ---------------- Quiz Configuration ----------------
        item {
            SectionHeader("Quiz Configuration")

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    description = "Questions per quiz session",
                    clickable = { showQuestionsDialog = true }
                ) {
                    Text(
                        "${uiState.maxQuestions}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }

    // ---------------- Dialogs ----------------
    if (showLangDialog) {
        SelectionDialog(
            title = "Select Language",
            options = languages,
            selectedOption = uiState.language,
            onOptionSelected = {
                viewModel.updateLanguage(it)
                showLangDialog = false
            },
            onDismiss = { showLangDialog = false }
        )
    }

    if (showQuestionsDialog) {
        SelectionDialog(
            title = "Select Max Questions",
            options = questionOptions.map { "$it" },
            selectedOption = uiState.maxQuestions.toString(),
            onOptionSelected = {
                viewModel.updateMaxQuestions(it.toInt())
                showQuestionsDialog = false
            },
            onDismiss = { showQuestionsDialog = false }
        )
    }

    if (showPaletteDialog) {
        PaletteDialog(
            currentPalette = uiState.selectedPalette,
            onPaletteSelected = {
                viewModel.updatePalette(it)
                showPaletteDialog = false
            },
            onDismiss = { showPaletteDialog = false }
        )
    }
}

/* -----------------------------------------------------------
   🎛 Reusable Selection Dialog (Language / Max Questions)
----------------------------------------------------------- */
@Composable
fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                onOptionSelected(option)
                            }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == selectedOption,
                            onClick = { onOptionSelected(option) }
                        )
                        Text(
                            option,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

/* -----------------------------------------------------------
   🎨 Palette Dialog
----------------------------------------------------------- */
@Composable
fun PaletteDialog(
    currentPalette: String,
    onPaletteSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Select Color Palette", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                ) {
                    items(AllPalettes) { palette ->
                        val isSelected = palette.name == currentPalette
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(palette.lightColors.primary)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.outlineVariant,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .clickable {
                                        onPaletteSelected(palette.name)
                                    }
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                palette.name,
                                fontSize = 12.sp,
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

/* -----------------------------------------------------------
   🧱 UI Helpers
----------------------------------------------------------- */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 15.sp,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingCard(
    icon: ImageVector,
    title: String,
    description: String,
    clickable: (() -> Unit)? = null,
    action: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = clickable != null) { clickable?.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
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
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, fontSize = 15.sp, style = MaterialTheme.typography.titleMedium)
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
