package com.adriandeleon.kmp.template.auth

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FakeSessionRepositoryTest :
    FunSpec({
        test("starts signed out") {
            val repository = FakeSessionRepository()

            repository.state.value shouldBe FakeSessionRepository.State()
        }

        test("sign in marks the session authenticated") {
            val repository = FakeSessionRepository()

            repository.signIn()

            repository.state.value shouldBe
                FakeSessionRepository.State(
                    isAuthenticated = true,
                    method = FakeSessionRepository.Method.SignIn,
                )
        }

        test("sign up marks the session authenticated") {
            val repository = FakeSessionRepository()

            repository.signUp()

            repository.state.value shouldBe
                FakeSessionRepository.State(
                    isAuthenticated = true,
                    method = FakeSessionRepository.Method.SignUp,
                )
        }

        test("sign out clears the session") {
            val repository =
                FakeSessionRepository(
                    FakeSessionRepository.State(
                        isAuthenticated = true,
                        method = FakeSessionRepository.Method.SignIn,
                    )
                )

            repository.signOut()

            repository.state.value shouldBe FakeSessionRepository.State()
        }
    })
