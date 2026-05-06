package com.adriandeleon.kmp.template.posts

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewPostsComponent : PostsComponent {
    private val _uiState =
        MutableValue<PostsComponent.UiState>(PostsComponent.UiState.Content(previewPosts))
    override val uiState: Value<PostsComponent.UiState> = _uiState

    var retryCallCount = 0
        private set

    override fun onRetry() {
        retryCallCount++
    }

    fun setUiState(newState: PostsComponent.UiState) {
        _uiState.value = newState
    }

    companion object {
        val previewPosts =
            listOf(
                PostUiModel(id = "1", title = "First Post", body = "Body of the first post."),
                PostUiModel(id = "2", title = "Second Post", body = "Body of the second post."),
                PostUiModel(id = "3", title = "Third Post", body = "Body of the third post."),
            )
    }
}
