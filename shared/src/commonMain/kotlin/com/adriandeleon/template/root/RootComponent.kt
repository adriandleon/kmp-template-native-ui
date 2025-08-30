package com.adriandeleon.template.root

import com.adriandeleon.template.home.HomeComponent
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner

/**
 * The root component which loads from the MainActivity in Android and the AppDelegate in iOS
 *
 * @see DefaultRootComponent
 */
interface RootComponent : BackHandlerOwner {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        /** @param component Child component for the home screen */
        data class Home(val component: HomeComponent) : Child
    }
}