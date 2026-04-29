package com.adriandeleon.kmp.template.common

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.adriandeleon.kmp.template.db.AppDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

internal actual val platformDatabaseModule: Module = module {
    single<AppDatabase> {
        Room.databaseBuilder<AppDatabase>(
            name = NSHomeDirectory() + "/app.db",
            // Room on iOS needs an explicit SQLite driver.
        ).setDriver(BundledSQLiteDriver()).build()
    }
}
