package com.adriandeleon.kmp.template.common

import androidx.room.Room
import com.adriandeleon.kmp.template.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformDatabaseModule: Module = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            context = androidContext(),
            name = "app.db",
        ).build()
    }
}
