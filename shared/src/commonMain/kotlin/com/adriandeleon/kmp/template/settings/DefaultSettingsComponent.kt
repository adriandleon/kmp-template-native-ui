package com.adriandeleon.kmp.template.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

internal class DefaultSettingsComponent(componentContext: ComponentContext) :
    SettingsComponent, ComponentContext by componentContext {

    override val uiState: Value<SettingsComponent.UiState> =
        MutableValue(SettingsComponent.UiState())
}
