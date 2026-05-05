package com.adriandeleon.kmp.template.auth

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

/**
 * Provider-agnostic session stand-in for the template.
 *
 * Replace this class with a repository backed by your authentication provider. Keep the
 * [AuthComponent] output contract so the root gate remains independent from provider details.
 */
class FakeSessionRepository(initialState: State = State()) {

    private val mutableState = MutableValue(initialState)
    val state: Value<State> = mutableState

    fun signIn() {
        mutableState.value = State(isAuthenticated = true, method = Method.SignIn)
    }

    fun signUp() {
        mutableState.value = State(isAuthenticated = true, method = Method.SignUp)
    }

    fun signOut() {
        mutableState.value = State()
    }

    data class State(
        val isAuthenticated: Boolean = false,
        val method: Method? = null,
    )

    enum class Method {
        SignIn,
        SignUp,
    }
}
