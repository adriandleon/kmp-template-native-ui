package com.adriandeleon.kmp.template.posts.presentation.store

internal sealed interface PostsIntent {
    data object Retry : PostsIntent
}
