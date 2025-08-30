package com.adriandeleon.template.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.adriandeleon.template.home.HomeView
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun RootView(component: RootComponent, modifier: Modifier = Modifier) {
    val childStack by component.stack.subscribeAsState()

    MaterialTheme {
        Children(
            stack = childStack,
            modifier = modifier.fillMaxSize(),
            animation = stackAnimation(fade()),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Home -> HomeView(instance.component)
            }
        }
    }
}
