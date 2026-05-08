package com.adriandeleon.kmp.template.appstate

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * DataStore-backed startup state repository.
 *
 * The in-memory value updates immediately so navigation can react synchronously; DataStore then
 * persists the same state for future launches.
 */
internal class DataStoreAppStateRepository(
    private val dataStore: DataStore<Preferences>,
    dispatcherProvider: DispatcherProvider,
) : AppStateRepository {
    private val mutableState = MutableValue(AppState())
    private val ioDispatcher = dispatcherProvider.io
    private val scope = CoroutineScope(SupervisorJob() + ioDispatcher)
    private val mainDispatcher = dispatcherProvider.main

    override val state: Value<AppState> = mutableState

    init {
        scope.launch {
            dataStore.data.collect { preferences ->
                withContext(mainDispatcher) { mutableState.value = preferences.toAppState() }
            }
        }
    }

    override suspend fun loadInitialState(): AppState =
        withContext(ioDispatcher) {
            val appState = dataStore.data.first().toAppState()
            withContext(mainDispatcher) { mutableState.value = appState }
            appState
        }

    override fun setHasSeenOnboarding(hasSeenOnboarding: Boolean) {
        updateState { copy(hasSeenOnboarding = hasSeenOnboarding) }
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        updateState { copy(isAuthenticated = isAuthenticated) }
    }

    override fun setAuthRequired(authRequired: Boolean) {
        updateState { copy(authRequired = authRequired) }
    }

    override fun reset() {
        updateState { AppState() }
    }

    private fun updateState(transform: AppState.() -> AppState) {
        val nextState = mutableState.value.transform()
        mutableState.value = nextState
        scope.launch { dataStore.updateData { nextState.toPreferences() } }
    }

    private fun Preferences.toAppState(): AppState =
        AppState(
            hasSeenOnboarding = this[Keys.hasSeenOnboarding] ?: false,
            isAuthenticated = this[Keys.isAuthenticated] ?: false,
            authRequired = this[Keys.authRequired] ?: true,
        )

    private fun AppState.toPreferences(): Preferences =
        preferencesOf(
            Keys.hasSeenOnboarding to hasSeenOnboarding,
            Keys.isAuthenticated to isAuthenticated,
            Keys.authRequired to authRequired,
        )

    private object Keys {
        val hasSeenOnboarding = booleanPreferencesKey("has_seen_onboarding")
        val isAuthenticated = booleanPreferencesKey("is_authenticated")
        val authRequired = booleanPreferencesKey("auth_required")
    }
}
