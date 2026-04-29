package com.adriandeleon.kmp.template.posts.data.datasource

import com.adriandeleon.kmp.template.db.AppDatabase
import com.adriandeleon.kmp.template.db.post.PostEntity

internal interface PostsLocalDataSource {
    suspend fun getAll(): List<PostEntity>

    suspend fun insertAll(posts: List<PostEntity>)
}

internal class DefaultPostsLocalDataSource(private val database: AppDatabase) :
    PostsLocalDataSource {
    override suspend fun getAll(): List<PostEntity> = database.postDao().getAll()

    override suspend fun insertAll(posts: List<PostEntity>) = database.postDao().insertAll(posts)
}
