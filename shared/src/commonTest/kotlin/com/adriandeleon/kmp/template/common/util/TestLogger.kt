@file:OptIn(ExperimentalKermitApi::class)

package com.adriandeleon.kmp.template.common.util

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Severity
import co.touchlab.kermit.TestConfig
import co.touchlab.kermit.TestLogWriter
import com.adriandeleon.kmp.template.logger.data.KermitLoggerImpl

internal val testLogWriter = TestLogWriter(loggable = Severity.Verbose)

internal val kermitLogger: co.touchlab.kermit.Logger =
    co.touchlab.kermit.Logger(
        TestConfig(minSeverity = Severity.Debug, logWriterList = listOf(testLogWriter))
    )

internal val lastLogEntry: TestLogWriter.LogEntry
    get() = testLogWriter.logs.last()

internal val testLogger = KermitLoggerImpl(kermitLogger)
