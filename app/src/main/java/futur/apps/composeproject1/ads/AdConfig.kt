package futur.apps.composeproject1.ads

/**
 * ============================================================
 * 🎯 AdConfig.kt
 * ------------------------------------------------------------
 * Central configuration for all AdMob IDs.
 * Toggle [USE_TEST_ADS] for development vs production.
 * ============================================================
 */
object AdConfig {

    // ------------------------------------------------------------
    // 🔧 Switch between TEST and REAL AdMob IDs
    // ------------------------------------------------------------
    const val USE_TEST_ADS = true // ✅ change to false when publishing

    // ------------------------------------------------------------
    // 🧪 TEST IDs (Google official)
    // ------------------------------------------------------------
    private const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712"
    private const val TEST_REWARDED_ID = "ca-app-pub-3940256099942544/5224354917"

    // ------------------------------------------------------------
    // 🚀 REAL IDs (replace with your own later)
    // ------------------------------------------------------------
    private const val REAL_APP_ID = "ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"
    private const val REAL_INTERSTITIAL_ID = "ca-app-pub-xxxxxxxxxxxxxxxx/zzzzzzzzzz"
    private const val REAL_REWARDED_ID = "ca-app-pub-xxxxxxxxxxxxxxxx/wwwwwwwwww"

    // ------------------------------------------------------------
    // 🎯 Resolved IDs based on mode
    // ------------------------------------------------------------
    val APP_ID: String get() = if (USE_TEST_ADS) TEST_APP_ID else REAL_APP_ID
    val INTERSTITIAL_AD_UNIT_ID: String get() = if (USE_TEST_ADS) TEST_INTERSTITIAL_ID else REAL_INTERSTITIAL_ID
    val REWARDED_AD_UNIT_ID: String get() = if (USE_TEST_ADS) TEST_REWARDED_ID else REAL_REWARDED_ID
}
