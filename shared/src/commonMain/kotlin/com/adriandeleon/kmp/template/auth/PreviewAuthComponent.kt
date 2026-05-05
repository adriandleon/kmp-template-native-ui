package com.adriandeleon.kmp.template.auth

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

@Suppress("TooManyFunctions")
class PreviewAuthComponent(initialScreen: AuthComponent.Screen = AuthComponent.Screen.SignIn) :
    AuthComponent {

    private val mutableStack =
        MutableValue(ChildStack(configuration = initialScreen, instance = childFor(initialScreen)))
    override val stack: Value<ChildStack<AuthComponent.Screen, AuthComponent.Child>> = mutableStack

    private val mutableModalSlot =
        MutableValue(ChildSlot<AuthComponent.Screen, AuthComponent.ModalChild>())
    override val modalSlot: Value<ChildSlot<AuthComponent.Screen, AuthComponent.ModalChild>> =
        mutableModalSlot

    override fun openSignUp() {
        push(AuthComponent.Screen.SignUp)
    }

    override fun openForgotPassword() {
        push(AuthComponent.Screen.ForgotPassword)
    }

    override fun requestVerification() {
        push(AuthComponent.Screen.Verification)
    }

    override fun signIn() = Unit

    override fun signUp() = Unit

    override fun showTerms() {
        mutableModalSlot.value =
            ChildSlot(
                child =
                    Child.Created(
                        configuration = AuthComponent.Screen.Terms,
                        instance =
                            AuthComponent.ModalChild.Terms(
                                PreviewScreenComponent(AuthComponent.Screen.Terms)
                            ),
                    )
            )
    }

    override fun dismissModal() {
        mutableModalSlot.value = ChildSlot()
    }

    override fun back() {
        backTo(mutableStack.value.items.lastIndex - 1)
    }

    override fun backTo(index: Int) {
        val currentItems = mutableStack.value.items
        val selectedItems = currentItems.take((index + 1).coerceIn(1, currentItems.size))
        mutableStack.value =
            ChildStack(active = selectedItems.last(), backStack = selectedItems.dropLast(1))
    }

    private fun push(screen: AuthComponent.Screen) {
        val currentItems = mutableStack.value.items
        val child = Child.Created(configuration = screen, instance = childFor(screen))
        mutableStack.value = ChildStack(active = child, backStack = currentItems)
    }

    private fun childFor(screen: AuthComponent.Screen): AuthComponent.Child =
        when (screen) {
            AuthComponent.Screen.SignIn ->
                AuthComponent.Child.SignIn(PreviewScreenComponent(screen))
            AuthComponent.Screen.SignUp ->
                AuthComponent.Child.SignUp(PreviewScreenComponent(screen))
            AuthComponent.Screen.ForgotPassword ->
                AuthComponent.Child.ForgotPassword(PreviewScreenComponent(screen))
            AuthComponent.Screen.Verification ->
                AuthComponent.Child.Verification(PreviewScreenComponent(screen))
            AuthComponent.Screen.Terms -> error("Terms is displayed in modalSlot")
        }

    private class PreviewScreenComponent(override val screen: AuthComponent.Screen) :
        AuthComponent.ScreenComponent
}
