package com.adriandeleon.kmp.template.logger.data

import com.adriandeleon.kmp.template.logger.domain.Logger

internal class KermitLoggerImpl(private val kermitLogger: co.touchlab.kermit.Logger) : Logger {
    override fun error(throwable: Throwable?, message: () -> String) {
        kermitLogger.e(throwable) { message() }
    }

    override fun debug(throwable: Throwable?, message: () -> String) {
        kermitLogger.d(throwable) { message() }
    }

    override fun info(message: () -> String) {
        kermitLogger.i { message() }
    }
}
