package futur.apps.composeproject1._Mains

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import futur.apps.composeproject1.ads.AdsManager
import futur.apps.composeproject1.appScreens._Screens.LoadingScreen
import futur.apps.composeproject1.quizsystem.ui.theme.DynamicTheme
import futur.apps.composeproject1.viewmodels.SettingsViewModel
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
        adsManager.initializeAds(this)

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isLoaded by themeViewModel.isLoaded.collectAsState()
            val isDark by themeViewModel.isDarkTheme.collectAsState()
            val selectedPalette by themeViewModel.selectedPaletteName.collectAsState()

            if (!isLoaded) {
                LoadingScreen()
            } else {
                DynamicTheme(themeViewModel) {
                    MainScreen()
                }
            }
        }
    }
}
