package com.adriandeleon.kmp.template.appstate

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

/**
 * Non-persistent [AppStateRepository] for tests and previews.
 *
 * Keep app startup persistence behind [AppStateRepository] so apps can swap this implementation for
 * a DataStore-backed repository without changing navigation components.
 */
class InMemoryAppStateRepository(initialState: AppState = AppState()) : AppStateRepository {
    private val mutableState = MutableValue(initialState)

    override val state: Value<AppState> = mutableState

    override suspend fun loadInitialState(): AppState = mutableState.value

    override fun setHasSeenOnboarding(hasSeenOnboarding: Boolean) {
        mutableState.value = mutableState.value.copy(hasSeenOnboarding = hasSeenOnboarding)
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        mutableState.value = mutableState.value.copy(isAuthenticated = isAuthenticated)
    }

    override fun setAuthRequired(authRequired: Boolean) {
        mutableState.value = mutableState.value.copy(authRequired = authRequired)
    }

    override fun reset() {
        mutableState.value = AppState()
    }
}
