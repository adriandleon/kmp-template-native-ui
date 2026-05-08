package com.adriandeleon.kmp.template.root

import com.adriandeleon.kmp.template.appstate.AppState
import com.adriandeleon.kmp.template.appstate.AppStateRepository
import com.adriandeleon.kmp.template.appstate.InMemoryAppStateRepository
import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.adriandeleon.kmp.template.common.util.activeSlotInstance
import com.adriandeleon.kmp.template.common.util.testComponentContext
import com.adriandeleon.kmp.template.posts.PreviewPostsComponent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.test.runTest

class DefaultRootComponentTest :
    FunSpec({
        context("startup gate") {
            test("shows startup until persisted app state finishes loading") {
                runTest {
                    val persistedState =
                        AppState(
                            hasSeenOnboarding = true,
                            isAuthenticated = true,
                            authRequired = true,
                        )
                    val repository = DelayedAppStateRepository(persistedState)
                    val component = rootComponent(repository)

                    component.activeChild().shouldBeInstanceOf<RootComponent.Child.Startup>()

                    repository.finishLoading()

                    component.activeChild().shouldBeInstanceOf<RootComponent.Child.Main>()
                }
            }

            test("falls back to onboarding when persisted app state fails to load") {
                val component = rootComponent(FailingAppStateRepository())

                component.activeChild().shouldBeInstanceOf<RootComponent.Child.Onboarding>()
            }

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
        dispatcherProvider = TestDispatcherProvider,
        postsComponentFactory = { PreviewPostsComponent() },
    )

private fun rootComponent(appStateRepository: AppStateRepository): RootComponent =
    DefaultRootComponent(
        componentContext = testComponentContext(),
        appStateRepository = appStateRepository,
        dispatcherProvider = TestDispatcherProvider,
        postsComponentFactory = { PreviewPostsComponent() },
    )

private fun RootComponent.activeChild(): RootComponent.Child = slot.activeSlotInstance()

private class DelayedAppStateRepository(private val loadedState: AppState) : AppStateRepository {
    private val loading = CompletableDeferred<Unit>()
    private val mutableState = MutableValue(AppState())

    override val state: Value<AppState> = mutableState

    fun finishLoading() {
        loading.complete(Unit)
    }

    override suspend fun loadInitialState(): AppState {
        loading.await()
        mutableState.value = loadedState
        return loadedState
    }

    override fun setHasSeenOnboarding(hasSeenOnboarding: Boolean) {
        mutableState.value = mutableState.value.copy(hasSeenOnboarding = hasSeenOnboarding)
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        mutableState.value = mutableState.value.copy(isAuthenticated = isAuthenticated)
    }

    override fun setAuthRequired(authRequired: Boolean) {
        mutableState.value = mutableState.value.copy(authRequired = authRequired)
    }

    override fun reset() {
        mutableState.value = AppState()
    }
}

private class FailingAppStateRepository : AppStateRepository {
    private val mutableState = MutableValue(AppState())

    override val state: Value<AppState> = mutableState

    override suspend fun loadInitialState(): AppState {
        error("Persisted app state is unavailable")
    }

    override fun setHasSeenOnboarding(hasSeenOnboarding: Boolean) {
        mutableState.value = mutableState.value.copy(hasSeenOnboarding = hasSeenOnboarding)
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        mutableState.value = mutableState.value.copy(isAuthenticated = isAuthenticated)
    }

    override fun setAuthRequired(authRequired: Boolean) {
        mutableState.value = mutableState.value.copy(authRequired = authRequired)
    }

    override fun reset() {
        mutableState.value = AppState()
    }
}

private object TestDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher = ImmediateDispatcher
    override val default: CoroutineDispatcher = ImmediateDispatcher
    override val io: CoroutineDispatcher = ImmediateDispatcher
}

private object ImmediateDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        block.run()
    }
}
