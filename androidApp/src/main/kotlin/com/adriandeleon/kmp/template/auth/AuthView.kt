package com.adriandeleon.kmp.template.auth

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriandeleon.kmp.template.R
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState

@Composable
fun AuthView(component: AuthComponent, modifier: Modifier = Modifier) {
    val modalSlot by component.modalSlot.subscribeAsState()

    Children(
        stack = component.stack,
        modifier =
            modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag(stringResource(R.string.tag_auth_screen)),
    ) { child ->
        AuthScreenContent(child.instance, component)
    }

    if (modalSlot.child?.instance is AuthComponent.ModalChild.Terms) {
        AlertDialog(
            onDismissRequest = component::dismissModal,
            title = { Text(stringResource(R.string.auth_terms_title)) },
            text = { Text(stringResource(R.string.auth_terms_body)) },
            confirmButton = {
                TextButton(
                    onClick = component::dismissModal,
                    modifier =
                        Modifier.testTag(stringResource(R.string.tag_auth_terms_close_button)),
                ) {
                    Text(stringResource(R.string.auth_terms_close_button))
                }
            },
        )
    }
}

@Composable
private fun AuthScreenContent(
    child: AuthComponent.Child,
    component: AuthComponent,
) {
    val screen = child.screen

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            if (screen != AuthComponent.Screen.SignIn) {
                TextButton(
                    onClick = component::back,
                    modifier = Modifier.testTag(stringResource(R.string.tag_auth_back_button)),
                ) {
                    Text(stringResource(R.string.auth_back_button))
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = screen.title(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.testTag(stringResource(R.string.tag_auth_title)),
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = screen.body(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.testTag(stringResource(R.string.tag_auth_body)),
            )
        }

        AuthActions(screen = screen, component = component)
    }
}

private val AuthComponent.Child.screen: AuthComponent.Screen
    get() =
        when (this) {
            is AuthComponent.Child.SignIn -> component.screen
            is AuthComponent.Child.SignUp -> component.screen
            is AuthComponent.Child.ForgotPassword -> component.screen
            is AuthComponent.Child.Verification -> component.screen
        }

@Composable
private fun AuthActions(
    screen: AuthComponent.Screen,
    component: AuthComponent,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        when (screen) {
            AuthComponent.Screen.SignIn -> SignInActions(component)
            AuthComponent.Screen.SignUp -> SignUpActions(component)
            AuthComponent.Screen.ForgotPassword -> ForgotPasswordActions(component)
            AuthComponent.Screen.Verification -> VerificationActions(component)
            AuthComponent.Screen.Terms -> Unit
        }
    }
}

@Composable
private fun SignInActions(component: AuthComponent) {
    Button(
        onClick = component::signIn,
        modifier = Modifier.fillMaxWidth().testTag(stringResource(R.string.tag_auth_primary_button)),
    ) {
        Text(stringResource(R.string.auth_sign_in_button))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedButton(
            onClick = component::openSignUp,
            modifier = Modifier.weight(1f).testTag(stringResource(R.string.tag_auth_secondary_button)),
        ) {
            Text(stringResource(R.string.auth_create_account_button))
        }
        OutlinedButton(
            onClick = component::openForgotPassword,
            modifier = Modifier.weight(1f).testTag(stringResource(R.string.tag_auth_forgot_button)),
        ) {
            Text(stringResource(R.string.auth_forgot_button))
        }
    }
}

@Composable
private fun SignUpActions(component: AuthComponent) {
    Button(
        onClick = component::signUp,
        modifier = Modifier.fillMaxWidth().testTag(stringResource(R.string.tag_auth_primary_button)),
    ) {
        Text(stringResource(R.string.auth_sign_up_button))
    }
    TextButton(
        onClick = component::showTerms,
        modifier =
            Modifier.testTag(stringResource(R.string.tag_auth_terms_button)),
    ) {
        Text(stringResource(R.string.auth_terms_button))
    }
}

@Composable
private fun ForgotPasswordActions(component: AuthComponent) {
    Button(
        onClick = component::requestVerification,
        modifier = Modifier.fillMaxWidth().testTag(stringResource(R.string.tag_auth_primary_button)),
    ) {
        Text(stringResource(R.string.auth_send_verification_button))
    }
}

@Composable
private fun VerificationActions(component: AuthComponent) {
    Button(
        onClick = component::back,
        modifier = Modifier.fillMaxWidth().testTag(stringResource(R.string.tag_auth_primary_button)),
    ) {
        Text(stringResource(R.string.auth_verification_done_button))
    }
}

@Composable
private fun AuthComponent.Screen.title(): String =
    when (this) {
        AuthComponent.Screen.SignIn -> stringResource(R.string.auth_sign_in_title)
        AuthComponent.Screen.SignUp -> stringResource(R.string.auth_sign_up_title)
        AuthComponent.Screen.ForgotPassword -> stringResource(R.string.auth_forgot_title)
        AuthComponent.Screen.Verification -> stringResource(R.string.auth_verification_title)
        AuthComponent.Screen.Terms -> stringResource(R.string.auth_terms_title)
    }

@Composable
private fun AuthComponent.Screen.body(): String =
    when (this) {
        AuthComponent.Screen.SignIn -> stringResource(R.string.auth_sign_in_body)
        AuthComponent.Screen.SignUp -> stringResource(R.string.auth_sign_up_body)
        AuthComponent.Screen.ForgotPassword -> stringResource(R.string.auth_forgot_body)
        AuthComponent.Screen.Verification -> stringResource(R.string.auth_verification_body)
        AuthComponent.Screen.Terms -> stringResource(R.string.auth_terms_body)
    }

@Preview(name = "Auth - Light - EN", locale = "en")
@Preview(name = "Auth - Light - ES", locale = "es-r419")
@Preview(name = "Auth - Light - PT", locale = "pt-rBR")
@Preview(name = "Auth - Dark - EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Auth - Dark - ES", locale = "es-r419", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Auth - Dark - PT", locale = "pt-rBR", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AuthPreview() {
    MaterialTheme { AuthView(PreviewAuthComponent()) }
}
