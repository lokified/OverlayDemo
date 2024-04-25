package com.loki.overlaydemo

import android.app.Application

class OverlayApp: Application() {

    companion object {
        lateinit var appModule: AppModuleImpl
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}