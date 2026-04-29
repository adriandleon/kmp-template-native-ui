package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.home.DefaultHomeComponent
import com.adriandeleon.kmp.template.home.HomeComponent
import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.root.RootComponent.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

/**
 * Default implementation of [RootComponent]
 *
 * @param componentContext context of this component
 * @see RootComponent
 */
class DefaultRootComponent(componentContext: ComponentContext) :
    RootComponent, KoinComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.Posts,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(configuration: Configuration, context: ComponentContext): Child =
        when (configuration) {
            is Configuration.Home -> Child.Home(homeComponent(context))
            is Configuration.Posts -> Child.Posts(postsComponent(context))
        }

    private fun homeComponent(componentContext: ComponentContext): HomeComponent =
        DefaultHomeComponent(componentContext = componentContext)

    private fun postsComponent(componentContext: ComponentContext): PostsComponent =
        get { parametersOf(componentContext) }

    @Serializable
    private sealed interface Configuration {

        @Serializable data object Home : Configuration

        @Serializable data object Posts : Configuration
    }
}
