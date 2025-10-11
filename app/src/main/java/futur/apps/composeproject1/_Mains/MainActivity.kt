package futur.apps.composeproject1._Mains

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import futur.apps.composeproject1.ads.AdsManager
import futur.apps.composeproject1.quizsystem.ui.theme.QuizSystemTheme
import futur.apps.composeproject1.viewmodels.ThemeViewModel

/**
 * MainActivity is the single-activity entry point of the application.
 * It sets up the Jetpack Compose environment, applies the current theme,
 * and initializes global managers (e.g., Ads).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val adsManager by lazy { AdsManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Ads
        adsManager.initializeAds(this)

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

            QuizSystemTheme(isDarkTheme) {
                MainScreen()
            }
        }
    }
}
