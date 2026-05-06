package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.appstate.AppState
import com.adriandeleon.kmp.template.appstate.InMemoryAppStateRepository
import com.adriandeleon.kmp.template.common.util.activeSlotInstance
import com.adriandeleon.kmp.template.common.util.testComponentContext
import com.adriandeleon.kmp.template.posts.PreviewPostsComponent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf

class DefaultRootComponentTest :
    FunSpec({
        context("startup gate") {
            test("shows onboarding when onboarding has not been seen and auth is not required") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = false,
                        authRequired = false,
                        isAuthenticated = false,
                    )

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Onboarding>()
            }

            test(
                "shows onboarding when onboarding has not been seen and signed out auth is required"
            ) {
                val component =
                    rootComponent(
                        hasSeenOnboarding = false,
                        authRequired = true,
                        isAuthenticated = false,
                    )

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Onboarding>()
            }

            test(
                "shows onboarding when onboarding has not been seen and signed in auth is required"
            ) {
                val component =
                    rootComponent(
                        hasSeenOnboarding = false,
                        authRequired = true,
                        isAuthenticated = true,
                    )

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Onboarding>()
            }

            test(
                "shows onboarding when onboarding has not been seen and signed in auth is not required"
            ) {
                val component =
                    rootComponent(
                        hasSeenOnboarding = false,
                        authRequired = false,
                        isAuthenticated = true,
                    )

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Onboarding>()
            }

            test("shows auth when onboarding is complete and required auth is missing") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = true,
                        authRequired = true,
                        isAuthenticated = false,
                    )

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Auth>()
            }

            test("shows main when onboarding is complete and required auth is present") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = true,
                        authRequired = true,
                        isAuthenticated = true,
                    )

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Main>()
            }

            test("shows main when onboarding is complete and auth is not required") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = true,
                        authRequired = false,
                        isAuthenticated = false,
                    )

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Main>()
            }
        }

        context("navigation updates") {
            test(
                "complete onboarding transitions to auth when authentication is required and missing"
            ) {
                val component =
                    rootComponent(
                        hasSeenOnboarding = false,
                        authRequired = true,
                        isAuthenticated = false,
                    )

                component.completeOnboarding()

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Auth>()
            }

            test("complete onboarding transitions to main when authentication is not required") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = false,
                        authRequired = false,
                        isAuthenticated = false,
                    )

                component.completeOnboarding()

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Main>()
            }

            test(
                "complete onboarding transitions to main when authentication is already complete"
            ) {
                val component =
                    rootComponent(
                        hasSeenOnboarding = false,
                        authRequired = true,
                        isAuthenticated = true,
                    )

                component.completeOnboarding()

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Main>()
            }

            test("complete authentication transitions to main") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = true,
                        authRequired = true,
                        isAuthenticated = false,
                    )

                component.completeAuthentication()

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Main>()
            }

            test("sign out transitions to auth when authentication is required") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = true,
                        authRequired = true,
                        isAuthenticated = true,
                    )

                component.signOut()

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Auth>()
            }

            test("reset onboarding transitions to onboarding") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = true,
                        authRequired = true,
                        isAuthenticated = true,
                    )

                component.resetOnboarding()

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Onboarding>()
            }

            test("set auth required false transitions to main") {
                val component =
                    rootComponent(
                        hasSeenOnboarding = true,
                        authRequired = true,
                        isAuthenticated = false,
                    )

                component.setAuthRequired(false)

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Main>()
            }
        }
    })

private fun rootComponent(
    hasSeenOnboarding: Boolean,
    authRequired: Boolean,
    isAuthenticated: Boolean,
): RootComponent =
    DefaultRootComponent(
        componentContext = testComponentContext(),
        appStateRepository =
            InMemoryAppStateRepository(
                initialState =
                    AppState(
                        hasSeenOnboarding = hasSeenOnboarding,
                        isAuthenticated = isAuthenticated,
                        authRequired = authRequired,
                    )
            ),
        postsComponentFactory = { PreviewPostsComponent() },
    )

private fun RootComponent.activeChild(): RootComponent.Child = slot.activeSlotInstance()
