package com.loki.overlaydemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.loki.overlaydemo.util.drawOverOtherAppEnabled
import com.loki.overlaydemo.util.startPermissionActivity
import com.loki.overlaydemo.window.Window

class ForegroundService : Service() {

    private lateinit var fullScreenNotificationView: FullScreenNotificationView

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet Implemented")
    }

    override fun onCreate() {
        super.onCreate()

        showNotification()

//        fullScreenNotificationView = FullScreenNotificationView(applicationContext)
//        fullScreenNotificationView.open()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent?.getStringExtra(INTENT_COMMAND) ?: ""

        if (command == INTENT_COMMAND_EXIT) {
            stopService(Intent(this, ForegroundService::class.java))
            return START_NOT_STICKY
        }

        showNotification()

        if (command == INTENT_COMMAND_START) {
            if (!drawOverOtherAppEnabled()){
                startPermissionActivity()
            } else {
                val window = Window(this)
                window.open()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        //fullScreenNotificationView.removeOverLay()
    }

    private fun showNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val startIntent = Intent(this, ForegroundService::class.java).apply {
                putExtra(INTENT_COMMAND, INTENT_COMMAND_START)
            }

            val exitIntent = Intent(this, ForegroundService::class.java).apply {
                putExtra(INTENT_COMMAND, INTENT_COMMAND_EXIT)
            }

            val exitPendingIntent = PendingIntent.getService(
                this, CODE_EXIT_INTENT, exitIntent, PendingIntent.FLAG_IMMUTABLE
            )

            val startPendingIntent = PendingIntent.getService(
                this, CODE_START_INTENT, startIntent, PendingIntent.FLAG_IMMUTABLE
            )

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
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .addAction(
                    NotificationCompat.Action(
                        0,
                        "Open",
                        startPendingIntent
                    )
                )
                .addAction(
                    NotificationCompat.Action(
                        0,
                        "exit",
                        exitPendingIntent
                    )
                )
                .build()

            startForeground(2, notification)
        }
    }

    companion object {
        private const val CHANNEL_NAME = "Full_Screen_Demo_channel"
        const val INTENT_COMMAND = "intent_command"
        const val INTENT_COMMAND_START = "intent_command_start"
        const val INTENT_COMMAND_EXIT = "intent_command_exit"
        const val CODE_EXIT_INTENT = 0
        const val CODE_START_INTENT = 1
    }
}