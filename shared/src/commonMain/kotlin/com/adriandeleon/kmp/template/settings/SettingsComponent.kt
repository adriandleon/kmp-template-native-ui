package com.adriandeleon.kmp.template.settings

import com.arkivanov.decompose.value.Value

/** Component for the settings tab. Replace this placeholder with app settings state. */
interface SettingsComponent {

    val uiState: Value<UiState>

    data class UiState(val isReady: Boolean = true)
}
