package com.adriandeleon.kmp.template.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.selectNext
import com.arkivanov.decompose.router.pages.selectPrev
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class DefaultOnboardingComponent(
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

    private val mutableState = MutableValue(pages.value.toState())
    override val state: Value<OnboardingComponent.State> = mutableState

    override fun next() {
        navigation.selectNext { newPages, _ -> mutableState.value = newPages.toState() }
    }

    override fun previous() {
        navigation.selectPrev { newPages, _ -> mutableState.value = newPages.toState() }
    }

    override fun skip() {
        onOutput(OnboardingComponent.Output.Completed)
    }

    override fun finish() {
        if (mutableState.value.isLastPage) {
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

    private fun Pages<OnboardingComponent.Page>.toState(): OnboardingComponent.State =
        items.toState(selectedIndex = selectedIndex)

    private fun ChildPages<OnboardingComponent.Page, OnboardingComponent.PageComponent>.toState():
        OnboardingComponent.State =
        items.map { it.configuration }.toState(selectedIndex = selectedIndex)

    private fun List<OnboardingComponent.Page>.toState(
        selectedIndex: Int
    ): OnboardingComponent.State =
        OnboardingComponent.State(
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
