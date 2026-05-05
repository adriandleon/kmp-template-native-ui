package com.adriandeleon.kmp.template.appstate

import org.koin.core.module.Module

internal const val APP_STATE_DATA_STORE_FILE = "app_state.preferences_pb"

internal expect val platformAppStateModule: Module
