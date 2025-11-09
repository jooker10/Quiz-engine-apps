package futur.apps.composeproject1.ads

import android.app.Activity
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ============================================================
 * 💰 AdsManager.kt  (Final coroutine-safe version)
 * ------------------------------------------------------------
 * Handles loading & showing Interstitial and Rewarded ads using
 * structured concurrency and lifecycle-safe reloads.
 * ============================================================
 */
class AdsManager @Inject constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false

    companion object {
        private const val TAG = "AdsManager"
    }

    // ------------------------------------------------------------
    // 🚀 Initialize Ads safely (coroutine-supported)
    // ------------------------------------------------------------
    suspend fun initializeAds(activity: Activity) {
        withContext(Dispatchers.Main) {
            if (!isInitialized) {
                MobileAds.initialize(activity) {
                    Log.d(TAG, "✅ Mobile Ads initialized")
                }
                isInitialized = true
            }
        }

        // preload ads in background
        withContext(Dispatchers.IO) {
            loadInterstitialAd(activity)
            loadRewardedAd(activity)
        }
    }

    // ------------------------------------------------------------
    // 📦 Load Interstitial Ad
    // ------------------------------------------------------------
    suspend fun loadInterstitialAd(activity: Activity) {
        withContext(Dispatchers.IO) {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                activity,
                AdConfig.INTERSTITIAL_AD_UNIT_ID,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d(TAG, "✅ Interstitial loaded")
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        interstitialAd = null
                        Log.w(TAG, "❌ Interstitial failed: ${adError.message}")
                    }
                }
            )
        }
    }

    // ------------------------------------------------------------
    // 🎬 Show Interstitial Ad
    // ------------------------------------------------------------
    fun showInterstitial(activity: Activity, onDismissed: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad == null) {
            Log.d(TAG, "Interstitial not ready")
            (activity as? ComponentActivity)?.lifecycleScope?.launch {
                loadInterstitialAd(activity)
            }
            onDismissed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                (activity as? ComponentActivity)?.lifecycleScope?.launch {
                    loadInterstitialAd(activity)
                }
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Interstitial failed to show: ${adError.message}")
                interstitialAd = null
                (activity as? ComponentActivity)?.lifecycleScope?.launch {
                    loadInterstitialAd(activity)
                }
                onDismissed()
            }
        }

        ad.show(activity)
    }

    // ------------------------------------------------------------
    // 🎁 Load Rewarded Ad
    // ------------------------------------------------------------
    suspend fun loadRewardedAd(activity: Activity) {
        withContext(Dispatchers.IO) {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                activity,
                AdConfig.REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Log.d(TAG, "✅ Rewarded ad loaded")
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        rewardedAd = null
                        Log.w(TAG, "❌ Rewarded failed: ${adError.message}")
                    }
                }
            )
        }
    }

    // ------------------------------------------------------------
    // 🎮 Show Rewarded Ad
    // ------------------------------------------------------------
    fun showRewardedAd(activity: Activity, onReward: (RewardItem) -> Unit) {
        val ad = rewardedAd
        if (ad == null) {
            Log.d(TAG, "Rewarded ad not ready, reloading…")
            (activity as? ComponentActivity)?.lifecycleScope?.launch {
                loadRewardedAd(activity)
            }
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                (activity as? ComponentActivity)?.lifecycleScope?.launch {
                    loadRewardedAd(activity)
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Rewarded ad failed: ${adError.message}")
                rewardedAd = null
                (activity as? ComponentActivity)?.lifecycleScope?.launch {
                    loadRewardedAd(activity)
                }
            }
        }

        ad.show(activity, OnUserEarnedRewardListener { reward ->
            onReward(reward)
        })
    }
}
