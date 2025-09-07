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

class AdsManager @Inject constructor() {

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    /** Initialize ads (optional: load them immediately if Activity available) */
    fun initializeAds(activity: Activity) {
        loadInterstitialAd(activity)
        loadRewardedAd(activity)
    }

    /** Load Interstitial ad */
    fun loadInterstitialAd(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            "ca-app-pub-3940256099942544/1033173712", // Test ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("AdsManager", "Interstitial loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    Log.d("AdsManager", "Interstitial failed to load: ${adError.message}")
                }
            }
        )
    }

    /** Show Interstitial ad */
    fun showInterstitial(activity: Activity, onDismissed: () -> Unit = {}) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    onDismissed()
                    loadInterstitialAd(activity) // reload for next time
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("AdsManager", "Interstitial failed to show: ${adError.message}")
                    onDismissed()
                }
            }
            interstitialAd?.show(activity)
        } else {
            Log.d("AdsManager", "Interstitial not ready")
            onDismissed()
        }
    }

    /** Load Rewarded ad */
    fun loadRewardedAd(activity: Activity) {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            activity,
            "ca-app-pub-3940256099942544/5224354917", // Test ID
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d("AdsManager", "Rewarded ad loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    Log.d("AdsManager", "Rewarded ad failed to load: ${adError.message}")
                }
            }
        )
    }

    /** Show Rewarded ad */
    fun showRewardedAd(activity: Activity, onReward: (RewardItem) -> Unit) {
        if (rewardedAd != null) {
            rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
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
            Log.d("AdsManager", "Rewarded ad not ready")
            loadRewardedAd(activity) // Retry loading
            }
        }
}