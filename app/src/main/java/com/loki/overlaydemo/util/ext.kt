package com.loki.overlaydemo.util

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.View
import com.loki.overlaydemo.window.DraggableTouchListener
import com.loki.overlaydemo.ForegroundService
import com.loki.overlaydemo.ForegroundService.Companion.INTENT_COMMAND
import com.loki.overlaydemo.PermissionActivity

fun Context.startFloatingService(command: String = "") {

    val intent = Intent(this, ForegroundService::class.java)

    if (command.isNotEmpty()) {
        intent.putExtra(INTENT_COMMAND, command)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        this.startForegroundService(intent)
    } else {
        this.startService(intent)
    }
}

fun Context.drawOverOtherAppEnabled(): Boolean {
    return Settings.canDrawOverlays(this)
}

fun Context.startPermissionActivity() {
    startActivity(
        Intent(this, PermissionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    )
}

fun View.registerDraggableListener(
    initialPosition: () -> Point,
    positionListener: (x: Int, y: Int) -> Unit
) {
    DraggableTouchListener(context, this, initialPosition, positionListener)
}