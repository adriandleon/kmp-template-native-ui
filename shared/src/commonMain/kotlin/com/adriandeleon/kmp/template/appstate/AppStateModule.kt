package com.adriandeleon.kmp.template.appstate

import org.koin.dsl.bind
import org.koin.dsl.module

internal val appStateModule = module {
    single { DataStoreAppStateRepository(dataStore = get(), dispatcherProvider = get()) } bind
        AppStateRepository::class
}
