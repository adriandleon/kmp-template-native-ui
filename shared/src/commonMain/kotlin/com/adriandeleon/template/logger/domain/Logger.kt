package com.adriandeleon.template.logger.domain

internal interface Logger {

    fun error(throwable: Throwable? = null, message: () -> String)

    fun debug(throwable: Throwable? = null, message: () -> String)

    fun info(message: () -> String)
}
