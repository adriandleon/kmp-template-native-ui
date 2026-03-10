package com.adriandeleon.kmp.template.main

import android.app.Application
import com.adriandeleon.kmp.template.common.androidModule
import com.adriandeleon.kmp.template.common.di.initKoin
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
