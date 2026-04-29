package com.adriandeleon.kmp.template.posts.presentation

import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.adriandeleon.kmp.template.common.util.testComponentContext
import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import com.adriandeleon.kmp.template.posts.domain.usecase.GetPostsUseCase
import com.adriandeleon.kmp.template.posts.presentation.mapper.PostsUiMapper
import com.adriandeleon.kmp.template.posts.presentation.store.PostsStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf

private class FakeDispatcherProvider : DispatcherProvider {
    override val main = Dispatchers.Unconfined
    override val default = Dispatchers.Unconfined
    override val io = Dispatchers.Unconfined
}

private class FakePostsRepository(private val posts: List<Post> = emptyList()) : PostsRepository {
    override fun getPosts() = flowOf(posts)
}

class DefaultPostsComponentTest : FunSpec({
    val dispatchers = FakeDispatcherProvider()
    val uiMapper = PostsUiMapper()

    test("initial state is Loading") {
        val storeFactory = PostsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            getPostsUseCase = GetPostsUseCase(FakePostsRepository()),
            dispatchers = dispatchers,
        )
        val component = DefaultPostsComponent(
            componentContext = testComponentContext(),
            storeFactory = storeFactory,
            uiMapper = uiMapper,
        )
        component.state.value.shouldBeInstanceOf<PostsUiState.Loading>()
    }

    test("state becomes Content when repository returns posts") {
        val posts = listOf(Post("1", "Title", "Body"))
        val storeFactory = PostsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            getPostsUseCase = GetPostsUseCase(FakePostsRepository(posts)),
            dispatchers = dispatchers,
        )
        val component = DefaultPostsComponent(
            componentContext = testComponentContext(),
            storeFactory = storeFactory,
            uiMapper = uiMapper,
        )
        component.state.value.shouldBeInstanceOf<PostsUiState.Content>()
    }
})
