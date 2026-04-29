package com.adriandeleon.kmp.template.db.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
internal data class PostEntity(@PrimaryKey val id: Int, val title: String, val body: String)
