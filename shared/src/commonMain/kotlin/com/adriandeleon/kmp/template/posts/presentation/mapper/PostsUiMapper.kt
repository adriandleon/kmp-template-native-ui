package com.adriandeleon.kmp.template.posts.presentation.mapper

import com.adriandeleon.kmp.template.posts.PostUiModel
import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.presentation.store.PostsState

internal class PostsUiMapper {
    fun map(state: PostsState): PostsUiState = when {
        state.isLoading -> PostsUiState.Loading
        state.error != null -> PostsUiState.Error(state.error)
        else -> PostsUiState.Content(
            posts = state.posts.map { post ->
                PostUiModel(id = post.id, title = post.title, body = post.body)
            },
        )
    }
}
