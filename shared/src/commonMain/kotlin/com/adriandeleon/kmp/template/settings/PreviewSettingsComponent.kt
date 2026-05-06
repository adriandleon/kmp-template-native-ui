package com.adriandeleon.kmp.template.settings

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewSettingsComponent : SettingsComponent {
    override val uiState: Value<SettingsComponent.UiState> =
        MutableValue(SettingsComponent.UiState())
}
