package com.adriandeleon.kmp.template.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.auth.AuthView
import com.adriandeleon.kmp.template.main.MainView
import com.adriandeleon.kmp.template.onboarding.OnboardingView
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun RootView(component: RootComponent, modifier: Modifier = Modifier) {
    val slot by component.slot.subscribeAsState()

    MaterialTheme {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (val child = slot.child?.instance) {
                is RootComponent.Child.Onboarding -> OnboardingView(component = child.component)
                is RootComponent.Child.Auth -> AuthView(component = child.component)
                is RootComponent.Child.Main -> MainView(component = child.component)
                null -> Text(stringResource(R.string.root_starting_placeholder))
            }
        }
    }
}
