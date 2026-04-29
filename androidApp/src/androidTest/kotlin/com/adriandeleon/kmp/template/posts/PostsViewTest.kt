package com.adriandeleon.kmp.template.posts

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class PostsViewTest {
    @get:Rule val composeTestRule = createComposeRule()

    private val component = PreviewPostsComponent()

    @Test
    fun verifyLoadingIndicatorIsDisplayed() {
        component.setState(PostsUiState.Loading)
        composeTestRule.launchPostsView(component) verify { loadingIndicatorIsDisplayed() }
    }

    @Test
    fun verifyPostsListIsDisplayedWhenContentState() {
        composeTestRule.launchPostsView(component) verify { postsListIsDisplayed() }
    }

    @Test
    fun verifyPostsListHasCorrectItemCount() {
        composeTestRule.launchPostsView(component) verify {
            postsListHasItemCount(PreviewPostsComponent.previewPosts.size)
        }
    }

    @Test
    fun verifyErrorViewIsDisplayedWhenErrorState() {
        component.setState(PostsUiState.Error("Something went wrong."))
        composeTestRule.launchPostsView(component) verify { errorViewIsDisplayed() }
    }

    @Test
    fun verifyRetryButtonIsDisplayedOnError() {
        component.setState(PostsUiState.Error("Something went wrong."))
        composeTestRule.launchPostsView(component) verify { retryButtonIsDisplayed() }
    }

    @Test
    fun verifyRetryCallbackIsInvokedOnRetryClick() {
        component.setState(PostsUiState.Error("Something went wrong."))
        composeTestRule.launchPostsView(component) { clickRetry() } verify { retryWasCalled() }
    }
}
