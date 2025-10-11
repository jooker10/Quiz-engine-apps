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
import androidx.core.view.WindowCompat

/**
 * ================================================
 * QuizSystemTheme.kt
 *
 * 🔹 Defines Light and Dark color schemes for the app.
 * 🔹 Applies Material3 theme to the app.
 * 🔹 Sets up modern edge-to-edge system bars (status/navigation)
 *   using ComponentActivity.enableEdgeToEdge() and SystemBarStyle.
 *
 * Buyer Notes:
 * - Customize your color schemes by modifying LightColorScheme and DarkColorScheme.
 * - No need to manually set window.statusBarColor/navigationBarColor; edge-to-edge handles it.
 * - Works automatically with darkTheme flag and system preference.
 * - Ensures a consistent modern look on Android 12+.
 * ================================================
 */

// -------------------------- LIGHT THEME --------------------------
val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,          // main primary color
    onPrimary = Color.White,         // text/icons on primary
    background = BackgroundLight,    // app background color

    // ... add other colors (secondary, background, surface, error, etc.)
    surface = SurfaceLight
)

// -------------------------- DARK THEME --------------------------
val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.Black,
    background = BackgroundDark,
    // ... add other colors
    surface = SurfaceDark
)

// -------------------------- THEME SETUP --------------------------
@Composable
fun QuizSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),  // auto-detect system dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            // Get the hosting activity
            val activity = view.context as? ComponentActivity ?: return@SideEffect

            // Make layout draw behind system bars (edge-to-edge)
        //    WindowCompat.setDecorFitsSystemWindows(activity.window, false)

            // Apply system bar colors dynamically using SystemBarStyle
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

    // Apply Material3 theme to content
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,   // typography defined elsewhere
        content = content
    )
}
