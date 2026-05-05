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
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun RootView(component: RootComponent, modifier: Modifier = Modifier) {
    val slot by component.slot.subscribeAsState()

    MaterialTheme {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Temporary placeholders until the platform UI flow tasks provide real screens.
            when (slot.child?.instance) {
                is RootComponent.Child.Onboarding -> Text(stringResource(R.string.root_onboarding_placeholder))
                is RootComponent.Child.Auth -> Text(stringResource(R.string.root_auth_placeholder))
                is RootComponent.Child.Main -> Text(stringResource(R.string.root_main_placeholder))
                null -> Text(stringResource(R.string.root_starting_placeholder))
            }
        }
    }
}
