package com.adriandeleon.kmp.template.posts

import com.arkivanov.decompose.value.Value

interface PostsComponent {
    val state: Value<PostsUiState>

    fun onRetry()
}
