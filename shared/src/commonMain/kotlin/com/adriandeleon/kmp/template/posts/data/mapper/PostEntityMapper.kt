package com.adriandeleon.kmp.template.posts.data.mapper

import com.adriandeleon.kmp.template.db.post.PostEntity
import com.adriandeleon.kmp.template.posts.data.dto.PostDto
import com.adriandeleon.kmp.template.posts.domain.model.Post

internal class PostEntityMapper {
    fun toEntity(dto: PostDto): PostEntity =
        PostEntity(id = dto.id, title = dto.title, body = dto.body)

    fun toDomain(entity: PostEntity): Post =
        Post(id = entity.id.toString(), title = entity.title, body = entity.body)
}
