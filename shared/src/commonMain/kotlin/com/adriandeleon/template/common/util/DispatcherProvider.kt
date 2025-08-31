package com.adriandeleon.template.common.util

import kotlinx.coroutines.CoroutineDispatcher

internal interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}

internal expect fun provideDispatcher(): DispatcherProvider
