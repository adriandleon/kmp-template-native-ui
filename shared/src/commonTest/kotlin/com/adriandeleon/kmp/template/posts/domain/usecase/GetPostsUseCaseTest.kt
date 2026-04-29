package com.adriandeleon.kmp.template.posts.domain.usecase

import com.adriandeleon.kmp.template.posts.domain.model.Post
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetPostsUseCaseTest : FunSpec({
    val mockRepository = mock<PostsRepository>()

    test("delegates to repository and returns its flow") {
        runTest {
            val expected = listOf(Post("1", "Title", "Body"))
            every { mockRepository.getPosts() } returns flowOf(expected)

            val useCase = GetPostsUseCase(mockRepository)
            val result = useCase().first()

            result shouldBe expected
        }
    }
})
