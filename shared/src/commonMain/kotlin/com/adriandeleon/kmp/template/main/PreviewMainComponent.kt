package com.adriandeleon.kmp.template.main

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewMainComponent(
    initialPage: MainComponent.Page = MainComponent.Page.Home
) : MainComponent {

    private val items =
        MainComponent.Page.entries.map { page ->
            Child.Created(configuration = page, instance = PreviewPageComponent(page))
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

    private val mutableState = MutableValue(mutablePages.value.toState())
    override val state: Value<MainComponent.State> = mutableState

    override fun selectPage(page: MainComponent.Page) {
        selectPage(MainComponent.Page.entries.indexOf(page))
    }

    override fun selectPage(index: Int) {
        mutablePages.value = mutablePages.value.copy(selectedIndex = index.coerceIn(items.indices))
        mutableState.value = mutablePages.value.toState()
    }

    private fun ChildPages<MainComponent.Page, MainComponent.PageComponent>.toState() =
        MainComponent.State(
            selectedPage = items[selectedIndex].configuration,
            selectedIndex = selectedIndex,
            pageCount = items.size,
        )

    private class PreviewPageComponent(override val page: MainComponent.Page) :
        MainComponent.PageComponent
}
