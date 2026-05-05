package com.adriandeleon.kmp.template.appstate

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class InMemoryAppStateRepositoryTest :
    FunSpec({
        test("starts with the provided app state") {
            val repository =
                InMemoryAppStateRepository(
                    initialState =
                        AppState(
                            hasSeenOnboarding = true,
                            isAuthenticated = true,
                            authRequired = false,
                        )
                )

            repository.state.value shouldBe
                AppState(hasSeenOnboarding = true, isAuthenticated = true, authRequired = false)
        }

        test("updates onboarding authentication and auth requirement independently") {
            val repository = InMemoryAppStateRepository()

            repository.setHasSeenOnboarding(true)
            repository.setAuthenticated(true)
            repository.setAuthRequired(false)

            repository.state.value shouldBe
                AppState(hasSeenOnboarding = true, isAuthenticated = true, authRequired = false)
        }

        test("reset restores a signed out first launch state") {
            val repository =
                InMemoryAppStateRepository(
                    initialState =
                        AppState(
                            hasSeenOnboarding = true,
                            isAuthenticated = true,
                            authRequired = false,
                        )
                )

            repository.reset()

            repository.state.value shouldBe AppState()
        }
    })
