package com.adriandeleon.kmp.template.posts.data.repository

import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.adriandeleon.kmp.template.posts.data.datasource.PostsLocalDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.PostsRemoteDataSource
import com.adriandeleon.kmp.template.posts.data.mapper.PostEntityMapper
import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

internal class DefaultPostsRepository(
    private val remoteDataSource: PostsRemoteDataSource,
    private val localDataSource: PostsLocalDataSource,
    private val mapper: PostEntityMapper,
    private val dispatchers: DispatcherProvider,
) : PostsRepository {
    override fun getPosts(): Flow<List<Post>> =
        flow {
                val cached = localDataSource.getAll().map { mapper.toDomain(it) }
                if (cached.isNotEmpty()) emit(cached)

                val remote = remoteDataSource.fetchPosts()
                val entities = remote.map { mapper.toEntity(it) }
                localDataSource.insertAll(entities)
                emit(entities.map { mapper.toDomain(it) })
            }
            .flowOn(dispatchers.io)
}
