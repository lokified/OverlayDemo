package com.loki.overlaydemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {

    private lateinit var fullScreenNotificationView: FullScreenNotificationView

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet Implemented")
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyForeground()
        } else {
            startForeground(1, Notification())
        }

        fullScreenNotificationView = FullScreenNotificationView(applicationContext)
        fullScreenNotificationView.open()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        fullScreenNotificationView.removeOverLay()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyForeground() {

        val channel = NotificationChannel(
            CHANNEL_NAME,
            "Full Screen Demo",
            NotificationManager.IMPORTANCE_MIN
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_NAME)
            .setContentTitle("Overlay Demo")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(2, notification)
    }

    companion object {
        private const val CHANNEL_NAME = "Full_Screen_Demo_channel"
    }
}