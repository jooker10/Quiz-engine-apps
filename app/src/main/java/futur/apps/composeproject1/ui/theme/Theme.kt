package futur.apps.composeproject1.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color.Black,

    secondary = Color(0xFF50E3C2),
    onSecondary = Color.Black,

    tertiary = Color(0xFFFFB300),
    onTertiary = Color.Black,

    surface = Color(0xFF121212), // أسطح داكنة
    onSurface = Color.White,

    background = Color(0xFF1E1E1E), // خلفية أغمق
    onBackground = Color.White,

    primaryContainer = Color(0xFF1565C0), // أزرق داكن للحاويات
    secondaryContainer = Color(0xFF00897B), // أخضر داكن

    error = Color(0xFFCF6679),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color.White,
    secondary = Color(0xFF50E3C2),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFB300),
    onTertiary = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1C1C),
    primaryContainer = Color(0xFFD0E5FD),
    secondaryContainer = Color(0xFFC8F7E5),
    error = Color(0xFFD32F2F),
    onError = Color.White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ComposeProject1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    /*val colorScheme = when {
        *//*dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }*/
    val colorScheme = if(darkTheme)  {DarkColorScheme} else {LightColorScheme}

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}