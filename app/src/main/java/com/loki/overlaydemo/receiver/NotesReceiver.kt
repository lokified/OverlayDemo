package com.loki.overlaydemo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotesReceiver(
    val update: () -> Unit
) : BroadcastReceiver(){

    override fun onReceive(p0: Context?, p1: Intent?) {
        update()
    }

    companion object {
        const val NOTES_RECEIVER_ACTION = "update_notes"
    }
}