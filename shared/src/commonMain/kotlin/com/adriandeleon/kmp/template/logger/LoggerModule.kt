package com.adriandeleon.kmp.template.logger

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Severity
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.platformLogWriter
import com.adriandeleon.kmp.template.BuildKonfig
import com.adriandeleon.kmp.template.logger.data.ConfigCatLoggerImpl
import com.adriandeleon.kmp.template.logger.data.KermitLoggerImpl
import com.adriandeleon.kmp.template.logger.domain.Logger
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val loggerModule = module {
    singleOf(::kermitLogger)
    singleOf(::KermitLoggerImpl) { bind<Logger>() }
    singleOf(::ConfigCatLoggerImpl) { bind<com.configcat.log.Logger>() }
}

private typealias KermitLogger = co.touchlab.kermit.Logger

@OptIn(ExperimentalKermitApi::class)
private fun kermitLogger(): KermitLogger {
    val logWriter =
        if (BuildKonfig.DEBUG) {
            platformLogWriter()
        } else {
            CrashlyticsLogWriter(minSeverity = Severity.Verbose)
        }

    KermitLogger.setLogWriters(logWriter)
    KermitLogger.setTag("TemplateApp")
    return co.touchlab.kermit.Logger
}
