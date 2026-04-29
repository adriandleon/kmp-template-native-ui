package com.adriandeleon.kmp.template.db.post

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface PostDao {
    @Query("SELECT * FROM posts") suspend fun getAll(): List<PostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(posts: List<PostEntity>)
}
