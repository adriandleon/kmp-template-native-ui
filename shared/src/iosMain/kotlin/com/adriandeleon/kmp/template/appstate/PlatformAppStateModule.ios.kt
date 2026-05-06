package com.adriandeleon.kmp.template.appstate

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

internal actual val platformAppStateModule: Module = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { "${NSHomeDirectory()}/Documents/$AppStateDataStoreFile".toPath() }
        )
    }
}
