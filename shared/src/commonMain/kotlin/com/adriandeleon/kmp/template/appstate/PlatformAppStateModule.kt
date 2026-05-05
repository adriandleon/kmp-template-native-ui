package com.adriandeleon.kmp.template.appstate

import org.koin.core.module.Module

internal const val AppStateDataStoreFile = "app_state.preferences_pb"

internal expect val platformAppStateModule: Module
