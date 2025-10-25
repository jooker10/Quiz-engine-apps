package futur.apps.composeproject1.ads

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import javax.inject.Inject

/**
 * ============================================================
 * 💰 AdsManager.kt
 * ------------------------------------------------------------
 * Handles loading & showing Interstitial and Rewarded Ads.
 * ============================================================
 */
class AdsManager @Inject constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var isInitialized = false

    // ------------------------------------------------------------
    // 🚀 Initialize Mobile Ads
    // ------------------------------------------------------------
    fun initializeAds(activity: Activity) {
        if (!isInitialized) {
            MobileAds.initialize(activity) { Log.d("AdsManager", "Mobile Ads initialized") }
            isInitialized = true
        }

        // Optionally preload ads
        loadInterstitialAd(activity)
        loadRewardedAd(activity)
    }

    // ------------------------------------------------------------
    // 📦 Load Interstitial Ad
    // ------------------------------------------------------------
    fun loadInterstitialAd(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            AdConfig.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("AdsManager", "✅ Interstitial loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Log.w("AdsManager", "❌ Interstitial failed: ${adError.message}")
                }
            }
        )
    }

    // ------------------------------------------------------------
    // 🎬 Show Interstitial Ad
    // ------------------------------------------------------------
    fun showInterstitial(activity: Activity, onDismissed: () -> Unit = {}) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("AdsManager", "Interstitial dismissed")
                    interstitialAd = null
                    loadInterstitialAd(activity)
                    onDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w("AdsManager", "Interstitial failed: ${adError.message}")
                    onDismissed()
                }
            }
            ad.show(activity)
        } else {
            Log.d("AdsManager", "Interstitial not ready")
            loadInterstitialAd(activity)
            onDismissed()
        }
    }

    // ------------------------------------------------------------
    // 🎁 Load Rewarded Ad
    // ------------------------------------------------------------
    fun loadRewardedAd(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            activity,
            AdConfig.REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d("AdsManager", "✅ Rewarded ad loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    Log.w("AdsManager", "❌ Rewarded failed: ${adError.message}")
                }
            }
        )
    }

    // ------------------------------------------------------------
    // 🎮 Show Rewarded Ad
    // ------------------------------------------------------------
    fun showRewardedAd(activity: Activity, onReward: (RewardItem) -> Unit) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    loadRewardedAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("AdsManager", "Rewarded ad failed to show: ${adError.message}")
                    loadRewardedAd(activity)
                }
            }
            rewardedAd?.show(activity, OnUserEarnedRewardListener { reward ->
                onReward(reward)
            })
        } else {
            Log.d("AdsManager", "Rewarded ad not ready, loading…")
            loadRewardedAd(activity)

            // ✅ Automatically show it once loaded
            RewardedAd.load(
                activity,
                AdConfig.REWARDED_AD_UNIT_ID,
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        ad.show(activity, OnUserEarnedRewardListener { reward ->
                            onReward(reward)
                        })
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d("AdsManager", "Rewarded ad failed: ${adError.message}")
                    }
                }
            )
        }
    }
}
