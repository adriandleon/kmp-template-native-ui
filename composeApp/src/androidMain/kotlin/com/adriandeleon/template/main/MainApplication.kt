package com.adriandeleon.template.main

import android.app.Application
import com.adriandeleon.template.common.androidModule
import com.adriandeleon.template.common.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MainApplication)
            modules(androidModule)
        }
    }
}
