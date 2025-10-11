package futur.apps.composeproject1.quizsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import futur.apps.composeproject1.quizsystem.core.QuizConfig

/**
 * ================================================
 * Colors.kt
 *
 * Centralized color definitions for the Quiz System app.
 * Buyers Notes:
 * - Easy to customize primary/secondary brand colors.
 * - Semantic colors for quiz correctness, progress, backgrounds.
 * - Designed for light/dark mode support.
 * - Functions allow automatic theme adaptation.
 * ================================================
 */

// -------------------------
// Brand / App Colors
// -------------------------
val PrimaryLight = Color(0xFF2196F3)   // Light mode primary
val PrimaryDark = Color(0xFF64B5F6)    // Dark mode primary

val SecondaryLight = Color(0xFFFFC107) // Accent color light
val SecondaryDark = Color(0xFFFFD54F)  // Accent color dark

// -------------------------
// Quiz Semantic Colors
// -------------------------

// Correct answer color
val CorrectLight = Color(0xFF4CAF50)
val CorrectDark = Color(0xFF81C784)

@Composable
fun correctAnswerColor(): Color {
    // Automatically returns correct color based on system theme
    return if (isSystemInDarkTheme()) CorrectDark else CorrectLight
}

// Wrong answer color
val WrongLight = Color(0xFFF44336)
val WrongDark = Color(0xFFE57373)

@Composable
fun wrongAnswerColor(): Color {
    // Automatically returns wrong color based on system theme
    return if (isSystemInDarkTheme()) WrongDark else WrongLight
}

// Progress / performance colors
@Composable
fun progressResultColor(percentage: Float): Color {
    // Returns color based on performance percentage
    return when {
        percentage >= QuizConfig.PERFECT_PERCENTAGE -> CorrectLight      // Green = Perfect
        percentage >= QuizConfig.GOOD_PERCENTAGE -> Color(0xFFFFEB3B)   // Yellow = Good
        percentage >= QuizConfig.PASSING_PERCENTAGE -> Color(0xFFFF9800)// Orange = Pass
        else -> WrongLight                                             // Red = Fail
    }
}

// -------------------------
// Backgrounds & Surfaces
// -------------------------
val BackgroundLight = Color(0xFFF5F5F5)
val BackgroundDark = Color(0xFF121212)

val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)

// -------------------------
// Tertiary / Highlight Colors
// -------------------------
val HighlightLight = Color(0xFF009688)  // e.g., Teal accent light
val HighlightDark = Color(0xFF26A69A)   // e.g., Teal accent dark

// -------------------------
// Neutral / Gray Colors
// -------------------------
val GrayLight = Color(0xFF9E9E9E)
val GrayDark = Color(0xFF616161)
