package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.auth.AuthComponent
import com.adriandeleon.kmp.template.main.MainComponent
import com.adriandeleon.kmp.template.onboarding.OnboardingComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner

/**
 * The root component which loads from the MainActivity in Android and the AppDelegate in iOS
 *
 * @see DefaultRootComponent
 */
interface RootComponent : BackHandlerOwner {

    val slot: Value<ChildSlot<*, Child>>

    val isStarting: Value<Boolean>

    fun completeOnboarding()

    fun completeAuthentication()

    fun signOut()

    fun resetOnboarding()

    fun setAuthRequired(authRequired: Boolean)

    sealed interface Child {

        data object Startup : Child

        data class Onboarding(val component: OnboardingComponent) : Child

        data class Auth(val component: AuthComponent) : Child

        data class Main(val component: MainComponent) : Child
    }
}

/** Creates the root component without exposing the default implementation. */
fun createRootComponent(componentContext: ComponentContext): RootComponent =
    DefaultRootComponent(componentContext)
