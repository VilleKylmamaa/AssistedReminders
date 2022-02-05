package com.ville.assistedreminders

import android.app.Application

class AssistedRemindersApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}

