package com.adriandeleon.kmp.template.root

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

    fun completeOnboarding()

    fun completeAuthentication()

    fun signOut()

    fun resetOnboarding()

    fun setAuthRequired(authRequired: Boolean)

    sealed interface Child {

        data class Onboarding(val component: OnboardingComponent) : Child

        data class Auth(val component: AuthComponent) : Child

        data class Main(val component: MainComponent) : Child
    }

    /** Placeholder for the onboarding flow component; later tasks replace it with the real flow. */
    interface OnboardingComponent

    /**
     * Placeholder for the authentication flow component; later tasks replace it with the real flow.
     */
    interface AuthComponent

    /**
     * Placeholder for the signed-in main flow component; later tasks replace it with the real flow.
     */
    interface MainComponent
}
