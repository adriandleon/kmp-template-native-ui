package com.adriandeleon.kmp.template.main

import com.adriandeleon.kmp.template.main.examples.DefaultExamplesComponent
import com.adriandeleon.kmp.template.main.examples.ExamplesComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

internal class DefaultMainComponent(componentContext: ComponentContext) :
    MainComponent, ComponentContext by componentContext {

    private val navigation = PagesNavigation<MainComponent.Page>()

    override val pages: Value<ChildPages<MainComponent.Page, MainComponent.PageComponent>> =
        childPages(
            source = navigation,
            serializer = MainComponent.Page.serializer(),
            initialPages = { Pages(items = mainPages, selectedIndex = 0) },
            childFactory = ::createPageComponent,
        )

    override val examples: ExamplesComponent
        get() =
            (pages.value.items.first { it.configuration == MainComponent.Page.Examples }.instance
                    as MainComponent.PageComponent.Examples)
                .component

    private val mutableUiState = MutableValue(pages.value.toUiState())
    override val uiState: Value<MainComponent.UiState> = mutableUiState

    override fun selectPage(page: MainComponent.Page) {
        selectPage(mainPages.indexOf(page))
    }

    override fun selectPage(index: Int) {
        val selectedIndex = index.coerceIn(mainPages.indices)
        navigation.select(selectedIndex) { newPages, _ ->
            mutableUiState.value = newPages.toUiState()
        }
    }

    private fun createPageComponent(
        page: MainComponent.Page,
        context: ComponentContext,
    ): MainComponent.PageComponent =
        when (page) {
            MainComponent.Page.Examples ->
                MainComponent.PageComponent.Examples(DefaultExamplesComponent(context))
            else -> MainComponent.PageComponent.Generic(page)
        }

    private fun Pages<MainComponent.Page>.toUiState(): MainComponent.UiState =
        items.toUiState(selectedIndex = selectedIndex)

    private fun ChildPages<MainComponent.Page, MainComponent.PageComponent>.toUiState():
        MainComponent.UiState =
        items.map { it.configuration }.toUiState(selectedIndex = selectedIndex)

    private fun List<MainComponent.Page>.toUiState(selectedIndex: Int): MainComponent.UiState =
        MainComponent.UiState(
            selectedPage = this[selectedIndex],
            selectedIndex = selectedIndex,
            pageCount = size,
        )

    private companion object {
        val mainPages =
            listOf(
                MainComponent.Page.Home,
                MainComponent.Page.Examples,
                MainComponent.Page.Adaptive,
                MainComponent.Page.Settings,
            )
    }
}
