package com.adriandeleon.kmp.template.auth

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

/** Generic authentication flow that demonstrates Child Stack plus an optional Child Slot. */
interface AuthComponent {

    val stack: Value<ChildStack<*, Child>>

    val modalSlot: Value<ChildSlot<*, ModalChild>>

    fun openSignUp()

    fun openForgotPassword()

    fun requestVerification()

    fun signIn()

    fun signUp()

    fun showTerms()

    fun dismissModal()

    fun back()

    fun backTo(index: Int)

    sealed interface Child {
        data class SignIn(val component: ScreenComponent) : Child

        data class SignUp(val component: ScreenComponent) : Child

        data class ForgotPassword(val component: ScreenComponent) : Child

        data class Verification(val component: ScreenComponent) : Child
    }

    sealed interface ModalChild {
        data class Terms(val component: ScreenComponent) : ModalChild
    }

    sealed interface Output {
        data object Authenticated : Output
    }

    interface ScreenComponent {
        val screen: Screen
    }

    enum class Screen {
        SignIn,
        SignUp,
        ForgotPassword,
        Verification,
        Terms,
    }
}
