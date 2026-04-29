package com.adriandeleon.kmp.template.posts.domain.usecase

import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow

internal class GetPostsUseCase(private val repository: PostsRepository) {
    operator fun invoke(): Flow<List<Post>> = repository.getPosts()
}
