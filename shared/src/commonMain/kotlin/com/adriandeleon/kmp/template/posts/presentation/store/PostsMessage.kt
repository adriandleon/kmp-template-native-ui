package com.adriandeleon.kmp.template.posts.presentation.store

import com.adriandeleon.kmp.template.posts.domain.model.Post

internal sealed interface PostsMessage {
    data object LoadingStarted : PostsMessage
    data class PostsLoaded(val posts: List<Post>) : PostsMessage
    data class PostsFailed(val error: String) : PostsMessage
}
