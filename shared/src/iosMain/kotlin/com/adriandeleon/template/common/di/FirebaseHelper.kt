package com.adriandeleon.template.common.di

import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import com.adriandeleon.template.BuildKonfig
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.crashlytics.crashlytics
import dev.gitlive.firebase.initialize

@Suppress("unused")
fun startCrashKiOS() {
    Firebase.initialize()
    if (BuildKonfig.DEBUG.not()) {
        Firebase.analytics.setAnalyticsCollectionEnabled(true)
        Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
        setCrashlyticsUnhandledExceptionHook()
    }
}
