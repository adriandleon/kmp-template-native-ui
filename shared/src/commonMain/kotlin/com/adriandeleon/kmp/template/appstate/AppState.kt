package com.adriandeleon.kmp.template.appstate

/**
 * App-level state used by the root component to decide which flow should be visible.
 *
 * Replace or extend these flags when a real app has different startup requirements.
 */
data class AppState(
    val hasSeenOnboarding: Boolean = false,
    val isAuthenticated: Boolean = false,
    val authRequired: Boolean = true,
)
