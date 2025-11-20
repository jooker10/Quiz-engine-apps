package futur.apps.composeproject1.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import futur.apps.composeproject1.R
import futur.apps.composeproject1._Mains.MainActivity
import futur.apps.composeproject1.dataStore.AppDataStore
import kotlinx.coroutines.flow.first

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {

        val dataStore = AppDataStore(applicationContext)
        val showReminder = dataStore.showReminderNotifications.first()
        if (!showReminder) return Result.success()

        val lastOpen = dataStore.lastOpenTime.first()
        val reminderSent = dataStore.reminderSent.first()

        val now = System.currentTimeMillis()
        val diff = now - lastOpen

        val twoDays = 2 * 24 * 60 * 60 * 1000L

        // واحد فقط: إذا لم يتم الإرسال بعد AND مر يومان
        if (!reminderSent && diff >= twoDays) {
            sendNotification()
            dataStore.setReminderSent(true)
        }

        return Result.success()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendNotification() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "reminder_channel")
            .setContentTitle("Ready for a new Quiz?")
            .setContentText("Come back and test your English again!")
            .setSmallIcon(R.drawable.notifications_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(1001, notification)
    }
}
