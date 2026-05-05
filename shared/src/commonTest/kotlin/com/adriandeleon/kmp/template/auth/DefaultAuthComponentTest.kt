package com.adriandeleon.kmp.template.auth

import com.adriandeleon.kmp.template.common.util.testComponentContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class DefaultAuthComponentTest :
    FunSpec({
        test("starts on sign in") {
            val component = authComponent()

            component.activeChild().shouldBeInstanceOf<AuthComponent.Child.SignIn>()
        }

        test("opens sign up on top of sign in") {
            val component = authComponent()

            component.openSignUp()

            component.stack.value.items.map { it.instance.screen() } shouldContainExactly
                listOf(AuthComponent.Screen.SignIn, AuthComponent.Screen.SignUp)
        }

        test("opens forgot password and verification") {
            val component = authComponent()

            component.openForgotPassword()
            component.requestVerification()

            component.stack.value.items.map { it.instance.screen() } shouldContainExactly
                listOf(
                    AuthComponent.Screen.SignIn,
                    AuthComponent.Screen.ForgotPassword,
                    AuthComponent.Screen.Verification,
                )
        }

        test("back removes the active child") {
            val component = authComponent()

            component.openForgotPassword()
            component.requestVerification()
            component.back()

            component.activeChild().shouldBeInstanceOf<AuthComponent.Child.ForgotPassword>()
        }

        test("back to index keeps the selected stack prefix") {
            val component = authComponent()

            component.openForgotPassword()
            component.requestVerification()
            component.backTo(0)

            component.activeChild().shouldBeInstanceOf<AuthComponent.Child.SignIn>()
        }

        test("terms modal can be shown and dismissed") {
            val component = authComponent()

            component.showTerms()

            component.modalSlot.value.child
                ?.instance
                .shouldBeInstanceOf<AuthComponent.ModalChild.Terms>()

            component.dismissModal()

            component.modalSlot.value.child shouldBe null
        }

        test("sign in authenticates and emits output") {
            val sessionRepository = FakeSessionRepository()
            val outputs = mutableListOf<AuthComponent.Output>()
            val component =
                authComponent(sessionRepository = sessionRepository, onOutput = outputs::add)

            component.signIn()

            sessionRepository.state.value shouldBe
                FakeSessionRepository.State(
                    isAuthenticated = true,
                    method = FakeSessionRepository.Method.SignIn,
                )
            outputs shouldContainExactly listOf(AuthComponent.Output.Authenticated)
        }

        test("sign up authenticates and emits output") {
            val sessionRepository = FakeSessionRepository()
            val outputs = mutableListOf<AuthComponent.Output>()
            val component =
                authComponent(sessionRepository = sessionRepository, onOutput = outputs::add)

            component.openSignUp()
            component.signUp()

            sessionRepository.state.value shouldBe
                FakeSessionRepository.State(
                    isAuthenticated = true,
                    method = FakeSessionRepository.Method.SignUp,
                )
            outputs shouldContainExactly listOf(AuthComponent.Output.Authenticated)
        }
    })

private fun authComponent(
    sessionRepository: FakeSessionRepository = FakeSessionRepository(),
    onOutput: (AuthComponent.Output) -> Unit = {},
): AuthComponent =
    DefaultAuthComponent(
        componentContext = testComponentContext(),
        sessionRepository = sessionRepository,
        onOutput = onOutput,
    )

private fun AuthComponent.activeChild(): AuthComponent.Child = stack.value.active.instance

private fun AuthComponent.Child.screen(): AuthComponent.Screen =
    when (this) {
        is AuthComponent.Child.SignIn -> component.screen
        is AuthComponent.Child.SignUp -> component.screen
        is AuthComponent.Child.ForgotPassword -> component.screen
        is AuthComponent.Child.Verification -> component.screen
    }
