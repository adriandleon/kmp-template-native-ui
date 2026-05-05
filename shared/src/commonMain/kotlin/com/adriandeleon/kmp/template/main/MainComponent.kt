package com.adriandeleon.kmp.template.main

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

/** Signed-in app shell that demonstrates Decompose Child Pages as native bottom tabs. */
interface MainComponent {

    val pages: Value<ChildPages<Page, PageComponent>>

    val state: Value<State>

    fun selectPage(page: Page)

    fun selectPage(index: Int)

    @Serializable
    enum class Page {
        Home,
        Examples,
        Adaptive,
        Settings,
    }

    data class State(
        val selectedPage: Page,
        val selectedIndex: Int,
        val pageCount: Int,
    )

    interface PageComponent {
        val page: Page
    }
}
