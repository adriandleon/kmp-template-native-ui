package com.adriandeleon.kmp.template.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

@Suppress("TooManyFunctions")
internal class DefaultAuthComponent(
    componentContext: ComponentContext,
    private val sessionRepository: FakeSessionRepository = FakeSessionRepository(),
    private val onOutput: (AuthComponent.Output) -> Unit,
) : AuthComponent, ComponentContext by componentContext {

    private val stackNavigation = StackNavigation<Configuration>()
    private val modalNavigation = SlotNavigation<ModalConfiguration>()

    override val stack: Value<ChildStack<*, AuthComponent.Child>> =
        childStack(
            source = stackNavigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.SignIn,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    override val modalSlot: Value<ChildSlot<*, AuthComponent.ModalChild>> =
        childSlot(
            source = modalNavigation,
            serializer = ModalConfiguration.serializer(),
            handleBackButton = true,
            childFactory = ::createModalChild,
        )

    override fun openSignUp() {
        stackNavigation.pushNew(Configuration.SignUp)
    }

    override fun openForgotPassword() {
        stackNavigation.pushNew(Configuration.ForgotPassword)
    }

    override fun requestVerification() {
        stackNavigation.pushNew(Configuration.Verification)
    }

    override fun signIn() {
        sessionRepository.signIn()
        onOutput(AuthComponent.Output.Authenticated)
    }

    override fun signUp() {
        sessionRepository.signUp()
        onOutput(AuthComponent.Output.Authenticated)
    }

    override fun showTerms() {
        modalNavigation.activate(ModalConfiguration.Terms)
    }

    override fun dismissModal() {
        modalNavigation.dismiss()
    }

    override fun back() {
        stackNavigation.pop()
    }

    override fun backTo(index: Int) {
        stackNavigation.popTo(index = index.coerceAtLeast(0))
    }

    private fun createChild(
        configuration: Configuration,
        context: ComponentContext,
    ): AuthComponent.Child =
        when (configuration) {
            Configuration.SignIn ->
                AuthComponent.Child.SignIn(screenComponent(AuthComponent.Screen.SignIn, context))
            Configuration.SignUp ->
                AuthComponent.Child.SignUp(screenComponent(AuthComponent.Screen.SignUp, context))
            Configuration.ForgotPassword ->
                AuthComponent.Child.ForgotPassword(
                    screenComponent(AuthComponent.Screen.ForgotPassword, context)
                )
            Configuration.Verification ->
                AuthComponent.Child.Verification(
                    screenComponent(AuthComponent.Screen.Verification, context)
                )
        }

    private fun createModalChild(
        configuration: ModalConfiguration,
        context: ComponentContext,
    ): AuthComponent.ModalChild =
        when (configuration) {
            ModalConfiguration.Terms ->
                AuthComponent.ModalChild.Terms(screenComponent(AuthComponent.Screen.Terms, context))
        }

    private fun screenComponent(screen: AuthComponent.Screen, componentContext: ComponentContext) =
        DefaultScreenComponent(screen = screen, componentContext = componentContext)

    @Serializable
    private sealed interface Configuration {
        @Serializable data object SignIn : Configuration

        @Serializable data object SignUp : Configuration

        @Serializable data object ForgotPassword : Configuration

        @Serializable data object Verification : Configuration
    }

    @Serializable
    private sealed interface ModalConfiguration {
        @Serializable data object Terms : ModalConfiguration
    }

    private class DefaultScreenComponent(
        override val screen: AuthComponent.Screen,
        componentContext: ComponentContext,
    ) : AuthComponent.ScreenComponent, ComponentContext by componentContext
}
