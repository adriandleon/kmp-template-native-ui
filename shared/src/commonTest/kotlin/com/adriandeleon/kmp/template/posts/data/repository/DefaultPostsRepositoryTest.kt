package com.adriandeleon.kmp.template.posts.data.repository

import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.adriandeleon.kmp.template.db.post.PostEntity
import com.adriandeleon.kmp.template.posts.data.datasource.PostsLocalDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.PostsRemoteDataSource
import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import com.adriandeleon.kmp.template.posts.data.mapper.PostEntityMapper
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

private class FakeDispatcherProvider : DispatcherProvider {
    override val main = Dispatchers.Unconfined
    override val default = Dispatchers.Unconfined
    override val io = Dispatchers.Unconfined
}

class DefaultPostsRepositoryTest :
    FunSpec({
        val mockRemote = mock<PostsRemoteDataSource>()
        val mockLocal = mock<PostsLocalDataSource>()
        val mapper = PostEntityMapper()
        val dispatchers = FakeDispatcherProvider()

        context("getPosts") {
            test("emits cached posts first when cache is not empty") {
                runTest {
                    val cachedEntities =
                        listOf(PostEntity(id = 1, title = "Cached", body = "Body"))
                    everySuspend { mockLocal.getAll() } returns cachedEntities
                    everySuspend { mockRemote.fetchPosts() } returns emptyList()
                    everySuspend { mockLocal.insertAll(any()) } returns Unit

                    val repo =
                        DefaultPostsRepository(mockRemote, mockLocal, mapper, dispatchers)
                    val first = repo.getPosts().first()

                    first shouldHaveSize 1
                    first[0].title shouldBe "Cached"
                }
            }

            test("emits remote posts after fetching and caching") {
                runTest {
                    val remoteDtos =
                        listOf(PostDto(id = 2, title = "Remote", body = "Body", userId = 1))
                    everySuspend { mockLocal.getAll() } returns emptyList()
                    everySuspend { mockRemote.fetchPosts() } returns remoteDtos
                    everySuspend { mockLocal.insertAll(any()) } returns Unit

                    val repo =
                        DefaultPostsRepository(mockRemote, mockLocal, mapper, dispatchers)
                    val posts = repo.getPosts().first()

                    posts shouldHaveSize 1
                    posts[0].title shouldBe "Remote"
                }
            }
        }
    })
