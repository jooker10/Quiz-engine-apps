package futur.apps.composeproject1._Mains

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import futur.apps.composeproject1.ui.theme.ComposeProject1Theme
import futur.apps.composeproject1.viewmodels.ThemeViewModel

/**
 * MainActivity is the single-activity entry point of the application.
 * It sets up the Jetpack Compose environment, applies the current theme,
 * and initializes the navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Retrieve the current theme from the ThemeViewModel (Dark / Light)
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            // Apply the app theme and load the main screen
            ComposeProject1Theme(isDarkTheme) {
                MainScreen()
            }
        }
    }
}