package com.loki.overlaydemo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.loki.overlaydemo.util.startFloatingService

class BootReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, p1: Intent?) {
        context?.startFloatingService()
    }
}