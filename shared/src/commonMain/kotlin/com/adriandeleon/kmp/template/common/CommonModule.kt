package com.adriandeleon.kmp.template.common

import com.adriandeleon.kmp.template.BuildKonfig
import com.adriandeleon.kmp.template.common.util.provideDispatcher
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val commonModule = module {
    factoryOf(::provideDispatcher)
    single<StoreFactory> {
        if (BuildKonfig.DEBUG) {
            LoggingStoreFactory(DefaultStoreFactory())
        } else {
            DefaultStoreFactory()
        }
    }
}
