package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.home.HomeComponent
import com.adriandeleon.kmp.template.posts.PostsComponent
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

        /** @param component Child component for the posts screen */
        data class Posts(val component: PostsComponent) : Child
    }
}
