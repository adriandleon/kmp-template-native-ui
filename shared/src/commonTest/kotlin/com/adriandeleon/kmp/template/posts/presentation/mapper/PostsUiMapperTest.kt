package com.adriandeleon.kmp.template.posts.presentation.mapper

import com.adriandeleon.kmp.template.posts.PostUiModel
import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.presentation.store.PostsState
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class PostsUiMapperTest :
    FunSpec({
        val mapper = PostsUiMapper()

        test("maps loading state to PostsUiState.Loading") {
            val result = mapper.map(PostsState(isLoading = true))
            result.shouldBeInstanceOf<PostsUiState.Loading>()
        }

        test("maps error state to PostsUiState.Error with correct message") {
            val result = mapper.map(PostsState(isLoading = false, error = "Oops"))
            result shouldBe PostsUiState.Error("Oops")
        }

        test("maps content state to PostsUiState.Content with correct items") {
            val posts = listOf(Post("1", "Title", "Body"))
            val result = mapper.map(PostsState(isLoading = false, posts = posts))
            result shouldBe PostsUiState.Content(posts = listOf(PostUiModel("1", "Title", "Body")))
        }
    })
