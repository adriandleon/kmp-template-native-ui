package com.adriandeleon.kmp.template.onboarding

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewOnboardingComponent(
    initialPage: OnboardingComponent.Page = OnboardingComponent.Page.Welcome
) : OnboardingComponent {

    private val items =
        OnboardingComponent.Page.entries.map { page ->
            Child.Created(configuration = page, instance = PreviewPageComponent(page))
        }

    private val mutablePages =
        MutableValue(
            ChildPages(
                items = items,
                selectedIndex = OnboardingComponent.Page.entries.indexOf(initialPage),
            )
        )

    override val pages:
        Value<ChildPages<OnboardingComponent.Page, OnboardingComponent.PageComponent>> =
        mutablePages

    private val mutableState = MutableValue(mutablePages.value.toState())
    override val state: Value<OnboardingComponent.State> = mutableState

    override fun next() {
        select((mutablePages.value.selectedIndex + 1).coerceAtMost(items.lastIndex))
    }

    override fun previous() {
        select((mutablePages.value.selectedIndex - 1).coerceAtLeast(0))
    }

    override fun skip() = Unit

    override fun finish() {
        if (!mutableState.value.isLastPage) next()
    }

    private fun select(index: Int) {
        mutablePages.value = mutablePages.value.copy(selectedIndex = index)
        mutableState.value = mutablePages.value.toState()
    }

    private fun ChildPages<OnboardingComponent.Page, OnboardingComponent.PageComponent>.toState() =
        OnboardingComponent.State(
            selectedPage = items[selectedIndex].configuration,
            selectedIndex = selectedIndex,
            pageCount = items.size,
            canGoPrevious = selectedIndex > 0,
            canGoNext = selectedIndex < items.lastIndex,
            isLastPage = selectedIndex == items.lastIndex,
        )

    private class PreviewPageComponent(override val page: OnboardingComponent.Page) :
        OnboardingComponent.PageComponent
}
