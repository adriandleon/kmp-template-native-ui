package com.adriandeleon.kmp.template.posts.presentation.mapper

import com.adriandeleon.kmp.template.posts.PostUiModel
import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.presentation.store.PostsState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class PostsUiMapperTest :
    FunSpec({
        val mapper = PostsUiMapper()

        test("maps loading state to PostsComponent.UiState.Loading") {
            val result = mapper.map(PostsState(isLoading = true))
            result.shouldBeInstanceOf<PostsComponent.UiState.Loading>()
        }

        test("maps error state to PostsComponent.UiState.Error with correct message") {
            val result = mapper.map(PostsState(isLoading = false, error = "Oops"))
            result shouldBe PostsComponent.UiState.Error("Oops")
        }

        test("maps content state to PostsComponent.UiState.Content with correct items") {
            val posts = listOf(Post("1", "Title", "Body"))
            val result = mapper.map(PostsState(isLoading = false, posts = posts))
            result shouldBe
                PostsComponent.UiState.Content(posts = listOf(PostUiModel("1", "Title", "Body")))
        }
    })
