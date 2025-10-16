package futur.apps.composeproject1.quizsystem.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

/**
 * =====================================================
 * QuizSystemTheme.kt
 *
 * Central theme setup for the Quiz System app.
 * - Material3 color schemes (Light / Dark)
 * - Blue-based design with accent tertiary tones
 * - Automatic dark mode and system bar styling
 * =====================================================
 */

// -------------------------- LIGHT THEME --------------------------
val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = Color.White,

    secondary = SecondaryLight,
    onSecondary = Color.White,

    tertiary = TertiaryLight,
    onTertiary = Color.White,

    background = BackgroundLight,
    onBackground = Color.Black,

    surface = SurfaceLight,
    onSurface = Color.Black,

    error = ErrorLight,
    onError = Color.White,

    outline = OutlineLight,
    inversePrimary = InversePrimaryLight
)

// -------------------------- DARK THEME --------------------------
val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.Black,

    secondary = SecondaryDark,
    onSecondary = Color.Black,

    tertiary = TertiaryDark,
    onTertiary = Color.Black,

    background = BackgroundDark,
    onBackground = Color.White,

    surface = SurfaceDark,
    onSurface = Color.White,

    error = ErrorDark,
    onError = Color.Black,

    outline = OutlineDark,
    inversePrimary = InversePrimaryDark
)

// -------------------------- THEME SETUP --------------------------
@Composable
fun QuizSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    palette: AppPalette = BluePalette, // default
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) palette.darkColors else palette.lightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? ComponentActivity ?: return@SideEffect
            activity.enableEdgeToEdge(
                statusBarStyle = if (darkTheme)
                    SystemBarStyle.dark(colorScheme.background.toArgb())
                else
                    SystemBarStyle.light(
                        scrim = colorScheme.surface.toArgb(),
                        darkScrim = colorScheme.surface.toArgb()
                    ),
                navigationBarStyle = if (darkTheme)
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

/*
@Composable
fun QuizSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
)
{
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? ComponentActivity ?: return@SideEffect

            activity.enableEdgeToEdge(
                statusBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(colorScheme.background.toArgb())
                } else {
                    SystemBarStyle.light(
                        scrim = colorScheme.surface.toArgb(),
                        darkScrim = colorScheme.surface.toArgb()
                    )
                },
                navigationBarStyle = if (darkTheme) {
                    SystemBarStyle.dark(colorScheme.background.toArgb())
                } else {
                    SystemBarStyle.light(
                        scrim = colorScheme.surface.toArgb(),
                        darkScrim = colorScheme.surface.toArgb()
                    )
                }
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
*/
