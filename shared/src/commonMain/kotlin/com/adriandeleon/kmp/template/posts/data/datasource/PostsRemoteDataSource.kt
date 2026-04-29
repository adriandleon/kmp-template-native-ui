package com.adriandeleon.kmp.template.posts.data.datasource

import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal interface PostsRemoteDataSource {
    suspend fun fetchPosts(): List<PostDto>
}

internal class DefaultPostsRemoteDataSource(private val client: HttpClient) :
    PostsRemoteDataSource {
    override suspend fun fetchPosts(): List<PostDto> =
        client.get("https://jsonplaceholder.typicode.com/posts").body()
}
