package com.adriandeleon.kmp.template.main

import com.adriandeleon.kmp.template.examples.PreviewExamplesComponent
import com.adriandeleon.kmp.template.home.PreviewHomeComponent
import com.adriandeleon.kmp.template.posts.PreviewPostsComponent
import com.adriandeleon.kmp.template.settings.PreviewSettingsComponent
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewMainComponent(initialPage: MainComponent.Page = MainComponent.Page.Home) :
    MainComponent {

    override val home = PreviewHomeComponent()

    override val examples = PreviewExamplesComponent()

    override val posts = PreviewPostsComponent()

    override val settings = PreviewSettingsComponent()

    private val items =
        MainComponent.Page.entries.map { page ->
            Child.Created(configuration = page, instance = previewPageComponent(page))
        }

    private val mutablePages =
        MutableValue(
            ChildPages(
                items = items,
                selectedIndex = MainComponent.Page.entries.indexOf(initialPage),
            )
        )
    override val pages: Value<ChildPages<MainComponent.Page, MainComponent.PageComponent>> =
        mutablePages

    private val mutableUiState = MutableValue(mutablePages.value.toUiState())
    override val uiState: Value<MainComponent.UiState> = mutableUiState

    override fun selectPage(page: MainComponent.Page) {
        selectPage(MainComponent.Page.entries.indexOf(page))
    }

    override fun selectPage(index: Int) {
        mutablePages.value = mutablePages.value.copy(selectedIndex = index.coerceIn(items.indices))
        mutableUiState.value = mutablePages.value.toUiState()
    }

    private fun ChildPages<MainComponent.Page, MainComponent.PageComponent>.toUiState() =
        MainComponent.UiState(
            selectedPage = items[selectedIndex].configuration,
            selectedIndex = selectedIndex,
            pageCount = items.size,
        )

    private fun previewPageComponent(page: MainComponent.Page): MainComponent.PageComponent =
        when (page) {
            MainComponent.Page.Home -> MainComponent.PageComponent.Home(home)
            MainComponent.Page.Examples -> MainComponent.PageComponent.Examples(examples)
            MainComponent.Page.Posts -> MainComponent.PageComponent.Posts(posts)
            MainComponent.Page.Settings -> MainComponent.PageComponent.Settings(settings)
        }
}
