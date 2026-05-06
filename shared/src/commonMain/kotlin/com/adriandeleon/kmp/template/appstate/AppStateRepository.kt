package com.adriandeleon.kmp.template.appstate

import com.arkivanov.decompose.value.Value

/**
 * Source of truth for app startup flags.
 *
 * The root component observes this state and switches between onboarding, authentication, and main
 * flows. Production templates should back this contract with DataStore so the startup decision
 * survives app restarts.
 */
interface AppStateRepository {
    val state: Value<AppState>

    fun setHasSeenOnboarding(hasSeenOnboarding: Boolean)

    fun setAuthenticated(isAuthenticated: Boolean)

    fun setAuthRequired(authRequired: Boolean)

    fun reset()
}
