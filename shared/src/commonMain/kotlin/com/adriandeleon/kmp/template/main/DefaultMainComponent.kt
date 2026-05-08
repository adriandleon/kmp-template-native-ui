package com.adriandeleon.kmp.template.main

import com.adriandeleon.kmp.template.examples.DefaultExamplesComponent
import com.adriandeleon.kmp.template.examples.ExamplesComponent
import com.adriandeleon.kmp.template.home.DefaultHomeComponent
import com.adriandeleon.kmp.template.home.HomeComponent
import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.settings.DefaultSettingsComponent
import com.adriandeleon.kmp.template.settings.SettingsComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

internal class DefaultMainComponent(
    componentContext: ComponentContext,
    postsComponentFactory: (ComponentContext) -> PostsComponent,
) : MainComponent, ComponentContext by componentContext {

    private val navigation = PagesNavigation<MainComponent.Page>()
    private val homeComponent = DefaultHomeComponent(componentContext)
    private val examplesComponent = DefaultExamplesComponent(componentContext)
    private val postsComponent = postsComponentFactory(componentContext)
    private val settingsComponent = DefaultSettingsComponent(componentContext)

    override val pages: Value<ChildPages<MainComponent.Page, MainComponent.PageComponent>> =
        childPages(
            source = navigation,
            serializer = MainComponent.Page.serializer(),
            initialPages = { Pages(items = mainPages, selectedIndex = 0) },
            childFactory = ::createPageComponent,
        )

    override val home: HomeComponent = homeComponent

    override val examples: ExamplesComponent = examplesComponent

    override val posts: PostsComponent = postsComponent

    override val settings: SettingsComponent = settingsComponent

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
        @Suppress("UNUSED_PARAMETER") context: ComponentContext,
    ): MainComponent.PageComponent =
        when (page) {
            MainComponent.Page.Home -> MainComponent.PageComponent.Home(homeComponent)
            MainComponent.Page.Examples -> MainComponent.PageComponent.Examples(examplesComponent)
            MainComponent.Page.Posts -> MainComponent.PageComponent.Posts(postsComponent)
            MainComponent.Page.Settings -> MainComponent.PageComponent.Settings(settingsComponent)
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
                MainComponent.Page.Posts,
                MainComponent.Page.Settings,
            )
    }
}
