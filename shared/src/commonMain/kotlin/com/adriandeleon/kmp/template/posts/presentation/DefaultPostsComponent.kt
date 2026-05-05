package com.adriandeleon.kmp.template.posts.presentation

import com.adriandeleon.kmp.template.posts.PostsComponent
import com.adriandeleon.kmp.template.posts.PostsUiState
import com.adriandeleon.kmp.template.posts.presentation.mapper.PostsUiMapper
import com.adriandeleon.kmp.template.posts.presentation.store.PostsIntent
import com.adriandeleon.kmp.template.posts.presentation.store.PostsStoreFactory
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultPostsComponent(
    componentContext: ComponentContext,
    private val storeFactory: PostsStoreFactory,
    private val uiMapper: PostsUiMapper,
) : PostsComponent, ComponentContext by componentContext {

    private val store = storeFactory.create()
    private val _state = MutableValue(uiMapper.map(store.state))
    override val state: Value<PostsUiState> = _state

    init {
        lifecycle.doOnDestroy(store::dispose)
        val scope = coroutineScope()
        scope.launch {
            store.stateFlow.collect { storeState -> _state.value = uiMapper.map(storeState) }
        }
    }

    override fun onRetry() {
        store.accept(PostsIntent.Retry)
    }
}
