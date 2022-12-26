package com.app.statuscontrol

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FirebaseApp: Application() {

    companion object {
        var INSTANCE: FirebaseApp? = null
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}