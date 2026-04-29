package com.adriandeleon.kmp.template.posts.data.mapper

import com.adriandeleon.kmp.template.db.post.PostEntity
import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PostEntityMapperTest : FunSpec({
    val mapper = PostEntityMapper()

    context("toEntity") {
        test("maps PostDto fields to PostEntity correctly") {
            val dto = PostDto(id = 1, title = "Title", body = "Body", userId = 10)
            val entity = mapper.toEntity(dto)
            entity.id shouldBe 1
            entity.title shouldBe "Title"
            entity.body shouldBe "Body"
        }
    }

    context("toDomain") {
        test("maps PostEntity fields to Post domain model correctly") {
            val entity = PostEntity(id = 2, title = "Post Title", body = "Post Body")
            val post = mapper.toDomain(entity)
            post.id shouldBe "2"
            post.title shouldBe "Post Title"
            post.body shouldBe "Post Body"
        }
    }
})
