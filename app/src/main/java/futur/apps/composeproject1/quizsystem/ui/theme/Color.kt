package futur.apps.composeproject1.quizsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import futur.apps.composeproject1.quizsystem.core.QuizConfig

/**
 * ======================================================
 * Colors.kt — Centralized color definitions for the app.
 *
 * Highlights:
 * - Blue-based brand theme (modern & calm)
 * - Consistent Material 3 semantic naming
 * - Quiz-specific semantic colors (correct, wrong, progress)
 * - Auto light/dark adaptation via composables
 * ======================================================
 */

// -------------------------
// Brand / Primary Colors
// -------------------------
val PrimaryLight = Color(0xFF1976D2)   // Deep Blue 700
val OnPrimaryLight = Color.White
val PrimaryDark = Color(0xFF90CAF9)    // Light Blue 300
val OnPrimaryDark = Color(0xFF0D1B2A)

// Secondary brand color (accent)
val SecondaryLight = Color(0xFF03A9F4) // Sky Blue
val SecondaryDark = Color(0xFF0288D1)

// -------------------------
// Quiz Semantic Colors
// -------------------------
val CorrectLight = Color(0xFF4CAF50)  // Green 500
val CorrectDark = Color(0xFF81C784)   // Green 300

@Composable
fun correctAnswerColor(): Color =
    if (isSystemInDarkTheme()) CorrectDark else CorrectLight

val WrongLight = Color(0xFFF44336)    // Red 500
val WrongDark = Color(0xFFE57373)     // Red 300

@Composable
fun wrongAnswerColor(): Color =
    if (isSystemInDarkTheme()) WrongDark else WrongLight

// Progress color based on performance
@Composable
fun progressResultColor(percentage: Float): Color {
    return when {
        percentage >= QuizConfig.PERFECT_PERCENTAGE -> CorrectLight
        percentage >= QuizConfig.GOOD_PERCENTAGE -> Color(0xFFFFEB3B)   // Yellow
        percentage >= QuizConfig.PASSING_PERCENTAGE -> Color(0xFFFF9800)// Orange
        else -> WrongLight
    }
}

// -------------------------
// Backgrounds & Surfaces
// -------------------------
val BackgroundLight = Color(0xFFF4F8FF)   // Light blue-gray
val OnBackgroundLight = Color(0xFF0D1B2A)

val BackgroundDark = Color(0xFF0D1117)    // Subtle dark navy
val OnBackgroundDark = Color(0xFFE3F2FD)

val SurfaceLight = Color.White
val OnSurfaceLight = Color(0xFF1E1E1E)

val SurfaceDark = Color(0xFF1C1F26)
val OnSurfaceDark = Color(0xFFE3F2FD)

// -------------------------
// Tertiary / Highlights
// -------------------------
val HighlightLight = Color(0xFF00BCD4)   // Cyan 500
val HighlightDark = Color(0xFF26C6DA)

val TertiaryLight = Color(0xFF0097A7)   // Cyan-blue accent (light)
val TertiaryDark = Color(0xFF26C6DA)    // Softer cyan-blue for dark mode

// -------------------------
// Neutral / Outline / Error
// -------------------------
val GrayLight = Color(0xFF9E9E9E)
val GrayDark = Color(0xFF616161)

val ErrorLight = Color(0xFFD32F2F)
val ErrorDark = Color(0xFFEF5350)

val OutlineLight = Color(0xFFB0BEC5)
val OutlineDark = Color(0xFF455A64)

// -------------------------
// Inverse / Misc
// -------------------------
val InversePrimaryLight = Color(0xFF546E7A)
val InversePrimaryDark = Color(0xFFBBDEFB)

// ==============================
// Predefined Color Palettes
// ==============================
data class AppPalette(
    val name: String,
    val lightColors: androidx.compose.material3.ColorScheme,
    val darkColors: androidx.compose.material3.ColorScheme
)

val BluePalette = AppPalette(
    name = "Blue",
    lightColors = lightColorScheme(
        primary = Color(0xFF1976D2),
        secondary = Color(0xFF03A9F4),
        tertiary = Color(0xFF0097A7),
        surface = Color.White,
        background = Color(0xFFF4F8FF),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFF90CAF9),
        secondary = Color(0xFF0288D1),
        tertiary = Color(0xFF26C6DA),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val GreenPalette = AppPalette(
    name = "Green",
    lightColors = lightColorScheme(
        primary = Color(0xFF2E7D32),
        secondary = Color(0xFF66BB6A),
        tertiary = Color(0xFF81C784),
        surface = Color.White,
        background = Color(0xFFF4FFF5),
        onSurface = Color(0xFF1B1B1B)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFF81C784),
        secondary = Color(0xFF4CAF50),
        tertiary = Color(0xFF388E3C),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val PurplePalette = AppPalette(
    name = "Purple",
    lightColors = lightColorScheme(
        primary = Color(0xFF7B1FA2),
        secondary = Color(0xFFBA68C8),
        tertiary = Color(0xFFE1BEE7),
        surface = Color.White,
        background = Color(0xFFF8F4FF),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFFCE93D8),
        secondary = Color(0xFFAB47BC),
        tertiary = Color(0xFF8E24AA),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val RedPalette = AppPalette(
    name = "Red",
    lightColors = lightColorScheme(
        primary = Color(0xFFD32F2F),
        secondary = Color(0xFFE57373),
        tertiary = Color(0xFFFFCDD2),
        surface = Color.White,
        background = Color(0xFFFFF5F5),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFFE57373),
        secondary = Color(0xFFD32F2F),
        tertiary = Color(0xFFEF9A9A),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val OrangePalette = AppPalette(
    name = "Orange",
    lightColors = lightColorScheme(
        primary = Color(0xFFF57C00),
        secondary = Color(0xFFFFB74D),
        tertiary = Color(0xFFFFCC80),
        surface = Color.White,
        background = Color(0xFFFFF8F1),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFFFFB74D),
        secondary = Color(0xFFF57C00),
        tertiary = Color(0xFFFFA726),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val YellowPalette = AppPalette(
    name = "Yellow",
    lightColors = lightColorScheme(
        primary = Color(0xFFFBC02D),
        secondary = Color(0xFFFFF176),
        tertiary = Color(0xFFFFF59D),
        surface = Color.White,
        background = Color(0xFFFFFDE7),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFFFFF176),
        secondary = Color(0xFFFBC02D),
        tertiary = Color(0xFFFFF59D),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val TealPalette = AppPalette(
    name = "Teal",
    lightColors = lightColorScheme(
        primary = Color(0xFF00796B),
        secondary = Color(0xFF26A69A),
        tertiary = Color(0xFF80CBC4),
        surface = Color.White,
        background = Color(0xFFE0F2F1),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFF26A69A),
        secondary = Color(0xFF00796B),
        tertiary = Color(0xFF4DB6AC),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val PinkPalette = AppPalette(
    name = "Pink",
    lightColors = lightColorScheme(
        primary = Color(0xFFC2185B),
        secondary = Color(0xFFF06292),
        tertiary = Color(0xFFF8BBD0),
        surface = Color.White,
        background = Color(0xFFFFF0F6),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFFF06292),
        secondary = Color(0xFFC2185B),
        tertiary = Color(0xFFF48FB1),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val IndigoPalette = AppPalette(
    name = "Indigo",
    lightColors = lightColorScheme(
        primary = Color(0xFF303F9F),
        secondary = Color(0xFF5C6BC0),
        tertiary = Color(0xFF9FA8DA),
        surface = Color.White,
        background = Color(0xFFE8EAF6),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFF5C6BC0),
        secondary = Color(0xFF303F9F),
        tertiary = Color(0xFF7986CB),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val BrownPalette = AppPalette(
    name = "Brown",
    lightColors = lightColorScheme(
        primary = Color(0xFF6D4C41),
        secondary = Color(0xFFA1887F),
        tertiary = Color(0xFFD7CCC8),
        surface = Color.White,
        background = Color(0xFFEFEBE9),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFFA1887F),
        secondary = Color(0xFF6D4C41),
        tertiary = Color(0xFFD7CCC8),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val GrayPalette = AppPalette(
    name = "Gray",
    lightColors = lightColorScheme(
        primary = Color(0xFF616161),
        secondary = Color(0xFF9E9E9E),
        tertiary = Color(0xFFBDBDBD),
        surface = Color.White,
        background = Color(0xFFF5F5F5),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFF9E9E9E),
        secondary = Color(0xFF616161),
        tertiary = Color(0xFFBDBDBD),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)

val CyanPalette = AppPalette(
    name = "Cyan",
    lightColors = lightColorScheme(
        primary = Color(0xFF00BCD4),
        secondary = Color(0xFF26C6DA),
        tertiary = Color(0xFF80DEEA),
        surface = Color.White,
        background = Color(0xFFE0F7FA),
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = darkColorScheme(
        primary = Color(0xFF26C6DA),
        secondary = Color(0xFF00BCD4),
        tertiary = Color(0xFF4DD0E1),
        surface = Color(0xFF1C1F26),
        background = Color(0xFF0D1117),
        onSurface = Color(0xFFE3F2FD)
    )
)


val AllPalettes = listOf(
    BluePalette, GreenPalette, PurplePalette,
    RedPalette, OrangePalette, YellowPalette,
    TealPalette, PinkPalette, IndigoPalette,
    BrownPalette, GrayPalette, CyanPalette
)
