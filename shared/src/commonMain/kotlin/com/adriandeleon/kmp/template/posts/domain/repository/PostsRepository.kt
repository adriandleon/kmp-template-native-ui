package com.adriandeleon.kmp.template.posts.domain.repository

import com.adriandeleon.kmp.template.posts.domain.model.Post
import kotlinx.coroutines.flow.Flow

internal interface PostsRepository {
    fun getPosts(): Flow<List<Post>>
}
