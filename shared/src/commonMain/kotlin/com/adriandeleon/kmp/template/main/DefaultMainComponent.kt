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

class DefaultMainComponent(componentContext: ComponentContext) :
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

    private val mutableState = MutableValue(pages.value.toState())
    override val state: Value<MainComponent.State> = mutableState

    override fun selectPage(page: MainComponent.Page) {
        selectPage(mainPages.indexOf(page))
    }

    override fun selectPage(index: Int) {
        val selectedIndex = index.coerceIn(mainPages.indices)
        navigation.select(selectedIndex) { newPages, _ -> mutableState.value = newPages.toState() }
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

    private fun Pages<MainComponent.Page>.toState(): MainComponent.State =
        items.toState(selectedIndex = selectedIndex)

    private fun ChildPages<MainComponent.Page, MainComponent.PageComponent>.toState():
        MainComponent.State = items.map { it.configuration }.toState(selectedIndex = selectedIndex)

    private fun List<MainComponent.Page>.toState(selectedIndex: Int): MainComponent.State =
        MainComponent.State(
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
