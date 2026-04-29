package com.adriandeleon.kmp.template.posts.presentation.store

import com.arkivanov.mvikotlin.core.store.Store

internal interface PostsStore : Store<PostsIntent, PostsState, Nothing>
