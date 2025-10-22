package futur.apps.composeproject1.quizsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import futur.apps.composeproject1.quizsystem.core.QuizConfig

/* ==========================================================
   🎨 COLORS — Full Material3 + Custom Quiz Semantics
   ========================================================== */

// -----------------------------------------------------------
// ✅ Quiz-specific semantic colors
// -----------------------------------------------------------
val CorrectLight = Color(0xFF4CAF50)
val CorrectDark = Color(0xFF81C784)

@Composable
fun correctAnswerColor(): Color =
    if (isSystemInDarkTheme()) CorrectDark else CorrectLight

val WrongLight = Color(0xFFF44336)
val WrongDark = Color(0xFFE57373)

@Composable
fun wrongAnswerColor(): Color =
    if (isSystemInDarkTheme()) WrongDark else WrongLight

@Composable
fun progressResultColor(percentage: Float): Color {
    return when {
        percentage >= QuizConfig.PERFECT_PERCENTAGE -> CorrectLight
        percentage >= QuizConfig.GOOD_PERCENTAGE -> Color(0xFFFFEB3B)
        percentage >= QuizConfig.PASSING_PERCENTAGE -> Color(0xFFFF9800)
        else -> WrongLight
    }
}

// -----------------------------------------------------------
// ✅ Palette Model
// -----------------------------------------------------------
data class AppPalette(
    val name: String,
    val lightColors: androidx.compose.material3.ColorScheme,
    val darkColors: androidx.compose.material3.ColorScheme
)

// -----------------------------------------------------------
// ✅ Helper functions for consistent palettes
// -----------------------------------------------------------
fun baseLightColorScheme(
    primary: Color,
    onPrimary: Color,
    secondary: Color,
    onSecondary: Color,
    tertiary: Color,
    onTertiary: Color,
    background: Color,
    onBackground: Color,
    surface: Color,
    onSurface: Color
) = lightColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primary.copy(alpha = 0.12f),
    onPrimaryContainer = primary,

    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondary.copy(alpha = 0.12f),
    onSecondaryContainer = secondary,

    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiary.copy(alpha = 0.12f),
    onTertiaryContainer = tertiary,

    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surface.copy(alpha = 0.9f),
    onSurfaceVariant = onSurface.copy(alpha = 0.7f),

    error = Color(0xFFD32F2F),
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFF1E1E1E),

    outline = Color(0xFFB0BEC5),
    inversePrimary = primary.copy(alpha = 0.8f)
)

fun baseDarkColorScheme(
    primary: Color,
    onPrimary: Color,
    secondary: Color,
    onSecondary: Color,
    tertiary: Color,
    onTertiary: Color,
    background: Color,
    onBackground: Color,
    surface: Color,
    onSurface: Color
) = darkColorScheme(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primary.copy(alpha = 0.25f),
    onPrimaryContainer = onPrimary,

    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondary.copy(alpha = 0.25f),
    onSecondaryContainer = onSecondary,

    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiary.copy(alpha = 0.25f),
    onTertiaryContainer = onTertiary,

    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surface.copy(alpha = 0.85f),
    onSurfaceVariant = onSurface.copy(alpha = 0.6f),

    error = Color(0xFFEF5350),
    onError = Color.Black,
    errorContainer = Color(0xFFB71C1C),
    onErrorContainer = Color.White,

    outline = Color(0xFF455A64),
    inversePrimary = primary.copy(alpha = 0.6f)
)

// -----------------------------------------------------------
// ✅ All Color Palettes (12 complete)
// -----------------------------------------------------------

val BluePalette = AppPalette(
    name = "Blue",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF1976D2),
        onPrimary = Color.White,
        secondary = Color(0xFF03A9F4),
        onSecondary = Color.White,
        tertiary = Color(0xFF0097A7),
        onTertiary = Color.White,
        background = Color(0xFFF4F8FF),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFF90CAF9),
        onPrimary = Color.Black,
        secondary = Color(0xFF0288D1),
        onSecondary = Color.Black,
        tertiary = Color(0xFF26C6DA),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val GreenPalette = AppPalette(
    name = "Green",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF2E7D32),
        onPrimary = Color.White,
        secondary = Color(0xFF66BB6A),
        onSecondary = Color.White,
        tertiary = Color(0xFF81C784),
        onTertiary = Color.Black,
        background = Color(0xFFF4FFF5),
        onBackground = Color(0xFF1B1B1B),
        surface = Color.White,
        onSurface = Color(0xFF1B1B1B)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFF81C784),
        onPrimary = Color.Black,
        secondary = Color(0xFF4CAF50),
        onSecondary = Color.Black,
        tertiary = Color(0xFF388E3C),
        onTertiary = Color.White,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val PurplePalette = AppPalette(
    name = "Purple",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF7B1FA2),
        onPrimary = Color.White,
        secondary = Color(0xFFBA68C8),
        onSecondary = Color.White,
        tertiary = Color(0xFFE1BEE7),
        onTertiary = Color.Black,
        background = Color(0xFFF8F4FF),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFFCE93D8),
        onPrimary = Color.Black,
        secondary = Color(0xFFAB47BC),
        onSecondary = Color.Black,
        tertiary = Color(0xFF8E24AA),
        onTertiary = Color.White,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val RedPalette = AppPalette(
    name = "Red",
    lightColors = baseLightColorScheme(
        primary = Color(0xFFD32F2F),
        onPrimary = Color.White,
        secondary = Color(0xFFE57373),
        onSecondary = Color.White,
        tertiary = Color(0xFFFFCDD2),
        onTertiary = Color.Black,
        background = Color(0xFFFFF5F5),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFFE57373),
        onPrimary = Color.Black,
        secondary = Color(0xFFD32F2F),
        onSecondary = Color.White,
        tertiary = Color(0xFFEF9A9A),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val OrangePalette = AppPalette(
    name = "Orange",
    lightColors = baseLightColorScheme(
        primary = Color(0xFFF57C00),
        onPrimary = Color.White,
        secondary = Color(0xFFFFB74D),
        onSecondary = Color.Black,
        tertiary = Color(0xFFFFCC80),
        onTertiary = Color.Black,
        background = Color(0xFFFFF8F1),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFFFFB74D),
        onPrimary = Color.Black,
        secondary = Color(0xFFF57C00),
        onSecondary = Color.Black,
        tertiary = Color(0xFFFFA726),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val YellowPalette = AppPalette(
    name = "Yellow",
    lightColors = baseLightColorScheme(
        primary = Color(0xFFFBC02D),
        onPrimary = Color.Black,
        secondary = Color(0xFFFFF176),
        onSecondary = Color.Black,
        tertiary = Color(0xFFFFF59D),
        onTertiary = Color.Black,
        background = Color(0xFFFFFDE7),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFFFFF176),
        onPrimary = Color.Black,
        secondary = Color(0xFFFBC02D),
        onSecondary = Color.Black,
        tertiary = Color(0xFFFFF59D),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val TealPalette = AppPalette(
    name = "Teal",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF00796B),
        onPrimary = Color.White,
        secondary = Color(0xFF26A69A),
        onSecondary = Color.White,
        tertiary = Color(0xFF80CBC4),
        onTertiary = Color.Black,
        background = Color(0xFFE0F2F1),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFF26A69A),
        onPrimary = Color.Black,
        secondary = Color(0xFF00796B),
        onSecondary = Color.White,
        tertiary = Color(0xFF4DB6AC),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val PinkPalette = AppPalette(
    name = "Pink",
    lightColors = baseLightColorScheme(
        primary = Color(0xFFC2185B),
        onPrimary = Color.White,
        secondary = Color(0xFFF06292),
        onSecondary = Color.White,
        tertiary = Color(0xFFF8BBD0),
        onTertiary = Color.Black,
        background = Color(0xFFFFF0F6),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFFF06292),
        onPrimary = Color.Black,
        secondary = Color(0xFFC2185B),
        onSecondary = Color.White,
        tertiary = Color(0xFFF48FB1),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val IndigoPalette = AppPalette(
    name = "Indigo",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF303F9F),
        onPrimary = Color.White,
        secondary = Color(0xFF5C6BC0),
        onSecondary = Color.White,
        tertiary = Color(0xFF9FA8DA),
        onTertiary = Color.Black,
        background = Color(0xFFE8EAF6),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFF5C6BC0),
        onPrimary = Color.Black,
        secondary = Color(0xFF303F9F),
        onSecondary = Color.White,
        tertiary = Color(0xFF7986CB),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val BrownPalette = AppPalette(
    name = "Brown",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF6D4C41),
        onPrimary = Color.White,
        secondary = Color(0xFFA1887F),
        onSecondary = Color.White,
        tertiary = Color(0xFFD7CCC8),
        onTertiary = Color.Black,
        background = Color(0xFFEFEBE9),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFFA1887F),
        onPrimary = Color.Black,
        secondary = Color(0xFF6D4C41),
        onSecondary = Color.White,
        tertiary = Color(0xFFD7CCC8),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val GrayPalette = AppPalette(
    name = "Gray",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF616161),
        onPrimary = Color.White,
        secondary = Color(0xFF9E9E9E),
        onSecondary = Color.Black,
        tertiary = Color(0xFFBDBDBD),
        onTertiary = Color.Black,
        background = Color(0xFFF5F5F5),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFF9E9E9E),
        onPrimary = Color.Black,
        secondary = Color(0xFF616161),
        onSecondary = Color.White,
        tertiary = Color(0xFFBDBDBD),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

val CyanPalette = AppPalette(
    name = "Cyan",
    lightColors = baseLightColorScheme(
        primary = Color(0xFF00BCD4),
        onPrimary = Color.White,
        secondary = Color(0xFF26C6DA),
        onSecondary = Color.White,
        tertiary = Color(0xFF80DEEA),
        onTertiary = Color.Black,
        background = Color(0xFFE0F7FA),
        onBackground = Color(0xFF1E1E1E),
        surface = Color.White,
        onSurface = Color(0xFF1E1E1E)
    ),
    darkColors = baseDarkColorScheme(
        primary = Color(0xFF26C6DA),
        onPrimary = Color.Black,
        secondary = Color(0xFF00BCD4),
        onSecondary = Color.White,
        tertiary = Color(0xFF4DD0E1),
        onTertiary = Color.Black,
        background = Color(0xFF0D1117),
        onBackground = Color(0xFFE3F2FD),
        surface = Color(0xFF1C1F26),
        onSurface = Color(0xFFE3F2FD)
    )
)

// -----------------------------------------------------------
// ✅ All available palettes
// -----------------------------------------------------------
val AllPalettes = listOf(
    BluePalette,
    GreenPalette,
    PurplePalette,
    RedPalette,
    OrangePalette,
    YellowPalette,
    TealPalette,
    PinkPalette,
    IndigoPalette,
    BrownPalette,
    GrayPalette,
    CyanPalette
)

