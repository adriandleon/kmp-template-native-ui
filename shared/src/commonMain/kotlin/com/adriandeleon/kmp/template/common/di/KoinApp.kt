package com.adriandeleon.kmp.template.common.di

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import co.touchlab.kermit.koin.KermitKoinLogger
import com.adriandeleon.kmp.template.BuildKonfig
import com.adriandeleon.kmp.template.analytics.analyticsModule
import com.adriandeleon.kmp.template.appstate.appStateModule
import com.adriandeleon.kmp.template.appstate.platformAppStateModule
import com.adriandeleon.kmp.template.common.commonModule
import com.adriandeleon.kmp.template.common.platformDatabaseModule
import com.adriandeleon.kmp.template.features.featureFlagModule
import com.adriandeleon.kmp.template.logger.loggerModule
import com.adriandeleon.kmp.template.network.networkModule
import com.adriandeleon.kmp.template.posts.postsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

/**
 * Initializes Koin for the common module.
 *
 * @param config Optional KoinAppDeclaration for additional configuration.
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(
            // Here the list of shared modules
            commonModule,
            appStateModule,
            platformAppStateModule,
            platformDatabaseModule,
            networkModule,
            featureFlagModule,
            analyticsModule,
            loggerModule,
            postsModule,
        )
        logger(KermitKoinLogger(koin.get()))
        CrashlyticsKotlin.setCustomValue("flavor", if (BuildKonfig.DEBUG) "debug" else "release")
    }
}
