package futur.apps.composeproject1.RoomDatabase

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import futur.apps.composeproject1.ads.AdsManager

/**
 * Base class for maintaining global application state.
 */
@HiltAndroidApp
class MyApp  : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}

    }
}