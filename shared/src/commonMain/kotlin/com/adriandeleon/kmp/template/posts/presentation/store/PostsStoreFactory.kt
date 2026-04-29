package com.adriandeleon.kmp.template.posts.presentation.store

import com.adriandeleon.kmp.template.common.util.DispatcherProvider
import com.adriandeleon.kmp.template.posts.domain.usecase.GetPostsUseCase
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch

internal class PostsStoreFactory(
    private val storeFactory: StoreFactory,
    private val getPostsUseCase: GetPostsUseCase,
    private val dispatchers: DispatcherProvider,
) {
    fun create(): PostsStore =
        object : PostsStore, Store<PostsIntent, PostsState, Nothing> by storeFactory.create(
            name = "PostsStore",
            initialState = PostsState(),
            bootstrapper = SimpleBootstrapper(Action.LoadPosts),
            executorFactory = { PostsExecutor(getPostsUseCase, dispatchers) },
            reducer = PostsReducer,
        ) {}

    private sealed interface Action {
        data object LoadPosts : Action
    }

    private class PostsExecutor(
        private val getPostsUseCase: GetPostsUseCase,
        private val dispatchers: DispatcherProvider,
    ) : CoroutineExecutor<PostsIntent, Action, PostsState, PostsMessage, Nothing>(
        mainContext = dispatchers.main,
    ) {
        override fun executeAction(action: Action) {
            when (action) {
                Action.LoadPosts -> loadPosts()
            }
        }

        override fun executeIntent(intent: PostsIntent) {
            when (intent) {
                PostsIntent.Retry -> loadPosts()
            }
        }

        private fun loadPosts() {
            scope.launch {
                // MVIKotlin requires store messages to be dispatched on main.
                // Repository work already moves to IO via Flow operators.
                dispatch(PostsMessage.LoadingStarted)
                try {
                    getPostsUseCase().collect { posts ->
                        dispatch(PostsMessage.PostsLoaded(posts))
                    }
                } catch (e: Exception) {
                    dispatch(PostsMessage.PostsFailed(e.message ?: "Unknown error"))
                }
            }
        }
    }

    private object PostsReducer : Reducer<PostsState, PostsMessage> {
        override fun PostsState.reduce(msg: PostsMessage): PostsState =
            when (msg) {
                PostsMessage.LoadingStarted -> copy(isLoading = true, error = null)
                is PostsMessage.PostsLoaded -> copy(isLoading = false, posts = msg.posts, error = null)
                is PostsMessage.PostsFailed -> copy(isLoading = false, error = msg.error)
            }
    }
}
