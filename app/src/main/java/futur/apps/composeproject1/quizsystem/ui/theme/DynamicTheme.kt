package futur.apps.composeproject1.quizsystem.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import futur.apps.composeproject1.viewmodels.ThemeViewModel

@Composable
fun DynamicTheme(
    themeViewModel: ThemeViewModel,
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val selectedPaletteName by themeViewModel.selectedPaletteName.collectAsState()

    val currentPalette = AllPalettes.find { it.name == selectedPaletteName } ?: BluePalette
    val colorScheme = if (isDarkTheme) currentPalette.darkColors else currentPalette.lightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,

        content = content
    )
}
