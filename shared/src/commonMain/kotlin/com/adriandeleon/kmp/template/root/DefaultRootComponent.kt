package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.home.DefaultHomeComponent
import com.adriandeleon.kmp.template.home.HomeComponent
import com.adriandeleon.kmp.template.root.RootComponent.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

/**
 * Default implementation of [RootComponent]
 *
 * @param componentContext context of this component
 * @see RootComponent
 */
class DefaultRootComponent(componentContext: ComponentContext) :
    RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.Home,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(configuration: Configuration, context: ComponentContext): Child =
        when (configuration) {
            is Configuration.Home -> {
                Child.Home(homeComponent(context))
            }
        }

    private fun homeComponent(componentContext: ComponentContext): HomeComponent =
        DefaultHomeComponent(componentContext = componentContext)

    @Serializable
    private sealed interface Configuration {

        @Serializable data object Home : Configuration
    }
}
