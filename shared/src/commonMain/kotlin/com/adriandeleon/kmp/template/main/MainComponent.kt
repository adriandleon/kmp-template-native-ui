package com.adriandeleon.kmp.template.main

import com.adriandeleon.kmp.template.examples.ExamplesComponent
import com.adriandeleon.kmp.template.home.HomeComponent
import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.settings.SettingsComponent
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

/** Signed-in app shell that demonstrates Decompose Child Pages as native bottom tabs. */
interface MainComponent {

    val pages: Value<ChildPages<Page, PageComponent>>

    val home: HomeComponent

    val examples: ExamplesComponent

    val posts: PostsComponent

    val settings: SettingsComponent

    val uiState: Value<UiState>

    fun selectPage(page: Page)

    fun selectPage(index: Int)

    @Serializable
    enum class Page {
        Home,
        Examples,
        Posts,
        Settings,
    }

    data class UiState(val selectedPage: Page, val selectedIndex: Int, val pageCount: Int)

    sealed interface PageComponent {
        val page: Page

        data class Home(val component: HomeComponent) : PageComponent {
            override val page: Page = Page.Home
        }

        data class Examples(val component: ExamplesComponent) : PageComponent {
            override val page: Page = Page.Examples
        }

        data class Posts(val component: PostsComponent) : PageComponent {
            override val page: Page = Page.Posts
        }

        data class Settings(val component: SettingsComponent) : PageComponent {
            override val page: Page = Page.Settings
        }
    }
}
