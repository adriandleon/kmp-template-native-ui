package com.adriandeleon.kmp.template.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.pages.selectNext
import com.arkivanov.decompose.router.pages.selectPrev
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

internal class DefaultOnboardingComponent(
    componentContext: ComponentContext,
    private val onOutput: (OnboardingComponent.Output) -> Unit,
) : OnboardingComponent, ComponentContext by componentContext {

    private val navigation = PagesNavigation<OnboardingComponent.Page>()

    override val pages =
        childPages(
            source = navigation,
            serializer = OnboardingComponent.Page.serializer(),
            initialPages = { Pages(items = onboardingPages, selectedIndex = 0) },
            childFactory = ::createPageComponent,
        )

    private val mutableUiState = MutableValue(pages.value.toUiState())
    override val uiState: Value<OnboardingComponent.UiState> = mutableUiState

    override fun next() {
        navigation.selectNext { newPages, _ -> mutableUiState.value = newPages.toUiState() }
    }

    override fun previous() {
        navigation.selectPrev { newPages, _ -> mutableUiState.value = newPages.toUiState() }
    }

    override fun selectPage(index: Int) {
        val selectedIndex = index.coerceIn(onboardingPages.indices)
        navigation.select(selectedIndex) { newPages, _ ->
            mutableUiState.value = newPages.toUiState()
        }
    }

    override fun skip() {
        onOutput(OnboardingComponent.Output.Completed)
    }

    override fun finish() {
        if (mutableUiState.value.isLastPage) {
            onOutput(OnboardingComponent.Output.Completed)
        } else {
            next()
        }
    }

    private fun createPageComponent(
        page: OnboardingComponent.Page,
        context: ComponentContext,
    ): OnboardingComponent.PageComponent =
        DefaultPageComponent(page = page, componentContext = context)

    private fun Pages<OnboardingComponent.Page>.toUiState(): OnboardingComponent.UiState =
        items.toUiState(selectedIndex = selectedIndex)

    private fun ChildPages<OnboardingComponent.Page, OnboardingComponent.PageComponent>.toUiState():
        OnboardingComponent.UiState =
        items.map { it.configuration }.toUiState(selectedIndex = selectedIndex)

    private fun List<OnboardingComponent.Page>.toUiState(
        selectedIndex: Int
    ): OnboardingComponent.UiState =
        OnboardingComponent.UiState(
            selectedPage = this[selectedIndex],
            selectedIndex = selectedIndex,
            pageCount = size,
            canGoPrevious = selectedIndex > 0,
            canGoNext = selectedIndex < lastIndex,
            isLastPage = selectedIndex == lastIndex,
        )

    private class DefaultPageComponent(
        override val page: OnboardingComponent.Page,
        componentContext: ComponentContext,
    ) : OnboardingComponent.PageComponent, ComponentContext by componentContext

    private companion object {
        val onboardingPages =
            listOf(
                OnboardingComponent.Page.Welcome,
                OnboardingComponent.Page.Organize,
                OnboardingComponent.Page.Customize,
            )
    }
}
