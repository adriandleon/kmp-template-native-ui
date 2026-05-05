package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.auth.AuthComponent.Output
import com.adriandeleon.kmp.template.auth.DefaultAuthComponent
import com.adriandeleon.kmp.template.appstate.AppState
import com.adriandeleon.kmp.template.appstate.AppStateRepository
import com.adriandeleon.kmp.template.main.DefaultMainComponent
import com.adriandeleon.kmp.template.onboarding.DefaultOnboardingComponent
import com.adriandeleon.kmp.template.onboarding.OnboardingComponent.Output as OnboardingOutput
import com.adriandeleon.kmp.template.root.RootComponent.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.ObserveLifecycleMode
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.subscribe
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Default implementation of [RootComponent]
 *
 * @param componentContext context of this component
 * @see RootComponent
 */
class DefaultRootComponent(
    componentContext: ComponentContext,
    appStateRepository: AppStateRepository? = null,
) : RootComponent, KoinComponent, ComponentContext by componentContext {

    private val stateRepository: AppStateRepository = appStateRepository ?: get()
    private val navigation = SlotNavigation<Configuration>()

    override val slot: Value<ChildSlot<*, Child>> =
        childSlot(
            source = navigation,
            serializer = Configuration.serializer(),
            initialConfiguration = { stateRepository.state.value.rootConfiguration() },
            handleBackButton = false,
            childFactory = ::createChild,
        )

    init {
        stateRepository.state.subscribe(
            lifecycle = lifecycle,
            mode = ObserveLifecycleMode.CREATE_DESTROY,
        ) { appState ->
            navigation.activate(appState.rootConfiguration())
        }
    }

    private fun createChild(configuration: Configuration, context: ComponentContext): Child =
        when (configuration) {
            is Configuration.Onboarding -> Child.Onboarding(onboardingComponent(context))
            is Configuration.Auth -> Child.Auth(authComponent(context))
            is Configuration.Main -> Child.Main(DefaultMainComponent(context))
        }

    private fun onboardingComponent(componentContext: ComponentContext) =
        DefaultOnboardingComponent(
            componentContext = componentContext,
            onOutput = ::onOnboardingOutput,
        )

    private fun authComponent(componentContext: ComponentContext) =
        DefaultAuthComponent(
            componentContext = componentContext,
            onOutput = ::onAuthOutput,
        )

    private fun onOnboardingOutput(output: OnboardingOutput) {
        when (output) {
            OnboardingOutput.Completed -> completeOnboarding()
        }
    }

    private fun onAuthOutput(output: Output) {
        when (output) {
            Output.Authenticated -> completeAuthentication()
        }
    }

    override fun completeOnboarding() {
        stateRepository.setHasSeenOnboarding(true)
        reevaluateNavigation()
    }

    override fun completeAuthentication() {
        stateRepository.setAuthenticated(true)
        reevaluateNavigation()
    }

    override fun signOut() {
        stateRepository.setAuthenticated(false)
        reevaluateNavigation()
    }

    override fun resetOnboarding() {
        stateRepository.setHasSeenOnboarding(false)
        reevaluateNavigation()
    }

    override fun setAuthRequired(authRequired: Boolean) {
        stateRepository.setAuthRequired(authRequired)
        reevaluateNavigation()
    }

    private fun reevaluateNavigation() {
        navigation.activate(stateRepository.state.value.rootConfiguration())
    }

    private fun AppState.rootConfiguration(): Configuration {
        if (!hasSeenOnboarding) return Configuration.Onboarding
        if (authRequired && !isAuthenticated) return Configuration.Auth
        return Configuration.Main
    }

    @Serializable
    private sealed interface Configuration {

        @Serializable data object Onboarding : Configuration

        @Serializable data object Auth : Configuration

        @Serializable data object Main : Configuration
    }
}
