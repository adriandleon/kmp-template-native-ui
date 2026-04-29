package com.adriandeleon.kmp.template.posts

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewPostsComponent : PostsComponent {
    private val _state = MutableValue<PostsUiState>(
        PostsUiState.Content(previewPosts)
    )
    override val state: Value<PostsUiState> = _state

    var retryCallCount = 0
        private set

    override fun onRetry() {
        retryCallCount++
    }

    fun setState(newState: PostsUiState) {
        _state.value = newState
    }

    companion object {
        val previewPosts = listOf(
            PostUiModel(id = "1", title = "First Post", body = "Body of the first post."),
            PostUiModel(id = "2", title = "Second Post", body = "Body of the second post."),
            PostUiModel(id = "3", title = "Third Post", body = "Body of the third post."),
        )
    }
}
