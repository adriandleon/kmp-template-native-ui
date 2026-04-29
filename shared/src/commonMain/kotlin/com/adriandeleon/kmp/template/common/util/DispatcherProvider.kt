package com.adriandeleon.kmp.template.common.util

import kotlinx.coroutines.CoroutineDispatcher

internal interface DispatcherProvider {
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val io: CoroutineDispatcher
}

internal expect fun provideDispatcher(): DispatcherProvider
