package futur.apps.composeproject1._Mains

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.android.AndroidEntryPoint
import futur.apps.composeproject1.ads.AdsManager
import futur.apps.composeproject1.quizsystem.ui.theme.AppTheme
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
        // ✅ Load ads early
        MobileAds.initialize(this) {}
        adsManager.loadInterstitialAd(this)
        adsManager.loadRewardedAd(this)

        // ✅ Enable Firestore offline persistence (prevents startup flicker)
        com.google.firebase.Firebase.firestore.apply {
            firestoreSettings = firestoreSettings {
                isPersistenceEnabled = true
            }
        }

        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isLoaded by themeViewModel.isLoaded.collectAsState()

            AppTheme(themeViewModel) {
                // ✅ MainScreen now handles all loading overlays globally
                MainScreen(isThemeReady = isLoaded)
            }
        }
    }
}
