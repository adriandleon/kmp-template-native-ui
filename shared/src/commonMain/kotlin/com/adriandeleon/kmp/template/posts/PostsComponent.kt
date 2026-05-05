package com.adriandeleon.kmp.template.posts

import com.arkivanov.decompose.value.Value

interface PostsComponent {
    val uiState: Value<UiState>

    fun onRetry()

    sealed class UiState {
        data object Loading : UiState()

        data class Content(val posts: List<PostUiModel>) : UiState()

        data class Error(val message: String) : UiState()
    }
}
