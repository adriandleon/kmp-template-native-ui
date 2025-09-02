package com.adriandeleon.template.common.di

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import co.touchlab.kermit.koin.KermitKoinLogger
import com.adriandeleon.template.BuildKonfig
import com.adriandeleon.template.analytics.analyticsModule
import com.adriandeleon.template.common.commonModule
import com.adriandeleon.template.features.featureFlagModule
import com.adriandeleon.template.logger.loggerModule
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
            featureFlagModule,
            analyticsModule,
            loggerModule,
        )
        logger(KermitKoinLogger(koin.get()))
        CrashlyticsKotlin.setCustomValue("flavor", if (BuildKonfig.DEBUG) "debug" else "release")
    }
}
