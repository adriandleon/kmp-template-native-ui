package com.adriandeleon.kmp.template.onboarding

import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

/** Generic onboarding flow that demonstrates Decompose Child Pages. */
interface OnboardingComponent {

    val pages: Value<ChildPages<Page, PageComponent>>

    val uiState: Value<UiState>

    fun next()

    fun previous()

    fun selectPage(index: Int)

    fun skip()

    /**
     * Advances through intermediate pages and emits [Output.Completed] from the final page. Replace
     * this policy if a product-specific flow needs validation before completion.
     */
    fun finish()

    @Serializable
    enum class Page {
        Welcome,
        Organize,
        Customize,
    }

    data class UiState(
        val selectedPage: Page,
        val selectedIndex: Int,
        val pageCount: Int,
        val canGoPrevious: Boolean,
        val canGoNext: Boolean,
        val isLastPage: Boolean,
    )

    sealed interface Output {
        data object Completed : Output
    }

    interface PageComponent {
        val page: Page
    }
}
