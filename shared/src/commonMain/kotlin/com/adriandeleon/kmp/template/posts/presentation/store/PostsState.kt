package com.adriandeleon.kmp.template.posts.presentation.store

import com.adriandeleon.kmp.template.posts.domain.model.Post

internal data class PostsState(
    val isLoading: Boolean = true,
    val posts: List<Post> = emptyList(),
    val error: String? = null,
)
