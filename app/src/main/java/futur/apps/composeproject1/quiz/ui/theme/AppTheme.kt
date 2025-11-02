package futur.apps.composeproject1.quiz.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import futur.apps.composeproject1.viewmodels.ThemeViewModel

/**
 * =====================================================
 * AppTheme.kt
 *
 * Unified Material3 theme for the entire app.
 * - Uses palette from ThemeViewModel (AppDataStore)
 * - Applies edge-to-edge system bar styling
 * - Supports dynamic light/dark modes
 * =====================================================
 */
@Composable
fun AppTheme(
    themeViewModel: ThemeViewModel,
    content: @Composable () -> Unit
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val selectedPaletteName by themeViewModel.selectedPaletteName.collectAsState()

    val palette = AllPalettes.find { it.name == selectedPaletteName } ?: BluePalette
    val colorScheme = if (isDarkTheme) palette.darkColors else palette.lightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? ComponentActivity ?: return@SideEffect
            activity.enableEdgeToEdge(
                statusBarStyle = if (isDarkTheme)
                    SystemBarStyle.dark(colorScheme.background.toArgb())
                else
                    SystemBarStyle.light(
                        scrim = colorScheme.surface.toArgb(),
                        darkScrim = colorScheme.surface.toArgb()
                    ),
                navigationBarStyle = if (isDarkTheme)
                    SystemBarStyle.dark(colorScheme.background.toArgb())
                else
                    SystemBarStyle.light(
                        scrim = colorScheme.surface.toArgb(),
                        darkScrim = colorScheme.surface.toArgb()
                    )
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
