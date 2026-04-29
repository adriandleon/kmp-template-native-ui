package com.adriandeleon.kmp.template.posts

sealed class PostsUiState {
    data object Loading : PostsUiState()
    data class Content(val posts: List<PostUiModel>) : PostsUiState()
    data class Error(val message: String) : PostsUiState()
}
