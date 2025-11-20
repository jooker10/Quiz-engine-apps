package futur.apps.composeproject1._Mains

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import dagger.hilt.android.AndroidEntryPoint
import futur.apps.composeproject1.ads.AdsManager
import futur.apps.composeproject1.quiz.ui.theme.AppTheme
import futur.apps.composeproject1.viewmodels.ThemeViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import futur.apps.composeproject1.notifications.ReminderWorker
import java.util.concurrent.TimeUnit

/**
 * MainActivity — Entry point of the app.
 * Initializes UI first, then background SDKs (Firestore + Ads).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val adsManager by lazy { AdsManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ⚡ Show the app immediately
        setContent {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            val isLoaded by themeViewModel.isLoaded.collectAsState()

            AppTheme(themeViewModel) {
                MainScreen(isThemeReady = isLoaded)
            }
        }

        // ⚙️ Background initialization
        lifecycleScope.launch {
            try {
                // Enable Firestore offline caching
                com.google.firebase.Firebase.firestore.apply {
                    firestoreSettings = firestoreSettings {
                        isPersistenceEnabled = true
                    }
                }

                // Initialize and preload ads
                adsManager.initializeAds(this@MainActivity)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        scheduleReminderWorker()
    }

    private fun scheduleReminderWorker() {
        val workManager = WorkManager.getInstance(this)

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(
            24, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "quiz_reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }


    private fun createReminderChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Quiz Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

}
