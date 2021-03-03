package iooojik.casein.custom.notification

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import iooojik.casein.R
import iooojik.casein.StaticVars


class CustomNotification(private val title: String, private val message: String, private val context: Context) {

    private val manager : NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val builder = NotificationCompat.Builder(context, StaticVars().NOTIFICATIONS_CHANNEL)

    init {
        build()
    }

    private fun build(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelIfNeeded(
                StaticVars().NOTIFICATIONS_CHANNEL,
                StaticVars().NOTIFICATIONS_CHANNEL
            )
        }

        builder
            .setAutoCancel(false)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setWhen(System.currentTimeMillis())
            .setContentTitle(title)
            .setContentText(message)
            .priority = NotificationCompat.PRIORITY_HIGH

    }

    private fun reBuild(){
        build()
    }

    fun makeNotification(){
        if (!isForeground(context))
            manager.notify(StaticVars().NOTIFICATION_ID, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelIfNeeded(channelId: String, channelName: String) {
        val chan = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(chan)
    }

    private fun isForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                return true
            }
        }
        return false
    }
}