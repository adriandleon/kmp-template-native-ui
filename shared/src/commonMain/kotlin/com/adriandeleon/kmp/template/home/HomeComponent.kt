package com.adriandeleon.kmp.template.home

import com.arkivanov.decompose.value.Value

/** Component for home screen. This is the main screen after onboarding. */
interface HomeComponent {

    /** Renderable state for the home screen. */
    val uiState: Value<UiState>

    /** UI-only state exposed to Android and iOS. */
    data class UiState(val title: String)
}
