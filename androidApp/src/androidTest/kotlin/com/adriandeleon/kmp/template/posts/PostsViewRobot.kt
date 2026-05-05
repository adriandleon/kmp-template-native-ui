package com.adriandeleon.kmp.template.posts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.util.onNodeWithTag
import org.junit.Assert.assertEquals

fun ComposeContentTestRule.launchPostsView(
    component: PreviewPostsComponent,
    block: PostsViewRobot.() -> Unit = {},
): PostsViewRobot {
    setContent { PostsView(component) }
    return PostsViewRobot(this, component).apply(block)
}

class PostsViewRobot(
    private val rule: ComposeContentTestRule,
    private val component: PreviewPostsComponent,
) {
    infix fun verify(block: PostsViewVerification.() -> Unit): PostsViewVerification {
        rule.waitForIdle()
        return PostsViewVerification(rule, component).apply(block)
    }

    fun clickRetry() {
        rule.onNodeWithTag(R.string.tag_posts_retry_button).performClick()
    }
}

class PostsViewVerification(
    private val rule: ComposeContentTestRule,
    private val component: PreviewPostsComponent,
) {
    fun loadingIndicatorIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_loading).assertIsDisplayed()
    }

    fun postsListIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_list).assertIsDisplayed()
    }

    fun errorViewIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_error).assertIsDisplayed()
    }

    fun retryButtonIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_posts_retry_button).assertIsDisplayed()
    }

    fun retryWasCalled(expectedCallCount: Int = 1) {
        assertEquals(expectedCallCount, component.retryCallCount)
    }

    fun postsListHasItemCount(expected: Int) {
        assertEquals(
            expected,
            (component.uiState.value as PostsComponent.UiState.Content).posts.size,
        )
    }
}
