package iooojik.casein.background

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import iooojik.casein.LogMessages
import iooojik.casein.background.process.WebSocketsService

@Suppress("DEPRECATION")
class SocketService(private val activity: Activity, private val serviceIntent: Intent) {

    private val logMessages = LogMessages()

    fun startSocketService(){
        //запуск сервиса
        if (!isServiceRunning(WebSocketsService::class.java)) activity.startService(serviceIntent)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        //проверяем, запущен ли сервис
        val manager = activity.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i(logMessages.SYSTEM_MESSAGE, "SOCKET SERVICE IS RUNNING")
                return true
            }
        }
        Log.i(logMessages.SYSTEM_MESSAGE, "SOCKET SERVICE IS NOT RUNNING")
        return false
    }
}