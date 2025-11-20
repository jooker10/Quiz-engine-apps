package futur.apps.composeproject1.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import futur.apps.composeproject1.R

object NotificationManagerHelper {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(context: Context, title: String, message: String) {

        val channelId = "reminder_channel"

        // Create channel for Android 8+
        val channel = NotificationChannel(
            channelId,
            "Daily Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Daily quiz reminder"
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val managerCompat = NotificationManagerCompat.from(context)

        // Android 13+ permission check (skip silently)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Do NOT request permission automatically.
                return
            }
        }

        managerCompat.notify(1001, notification)
    }
}

