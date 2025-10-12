package futur.apps.composeproject1.quizsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
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
