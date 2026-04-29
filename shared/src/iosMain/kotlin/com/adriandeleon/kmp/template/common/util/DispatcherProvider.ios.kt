package com.adriandeleon.kmp.template.common.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher")
private class IOSDispatcher : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Main
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.Default // iOS has no Dispatchers.IO
}

internal actual fun provideDispatcher(): DispatcherProvider = IOSDispatcher()
