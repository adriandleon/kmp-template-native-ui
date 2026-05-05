package com.adriandeleon.kmp.template.posts.presentation.mapper

import com.adriandeleon.kmp.template.posts.PostUiModel
import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.posts.presentation.store.PostsState

internal class PostsUiMapper {
    internal fun map(state: PostsState): PostsComponent.UiState =
        when {
            state.isLoading -> PostsComponent.UiState.Loading
            state.error != null -> PostsComponent.UiState.Error(state.error)
            else ->
                PostsComponent.UiState.Content(
                    posts =
                        state.posts.map { post ->
                            PostUiModel(id = post.id, title = post.title, body = post.body)
                        }
                )
        }
}
