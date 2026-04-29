package com.adriandeleon.kmp.template.posts.data.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class PostDto(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
)
