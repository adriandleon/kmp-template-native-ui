package com.adriandeleon.kmp.template.home

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewHomeComponent : HomeComponent {
    override val uiState: Value<HomeComponent.UiState> =
        MutableValue(HomeComponent.UiState(title = "Home Screen"))
}
