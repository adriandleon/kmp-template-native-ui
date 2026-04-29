package com.adriandeleon.kmp.template.posts

import com.adriandeleon.kmp.template.posts.data.datasource.DefaultPostsLocalDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.DefaultPostsRemoteDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.PostsLocalDataSource
import com.adriandeleon.kmp.template.posts.data.datasource.PostsRemoteDataSource
import com.adriandeleon.kmp.template.posts.data.mapper.PostEntityMapper
import com.adriandeleon.kmp.template.posts.data.repository.DefaultPostsRepository
import com.adriandeleon.kmp.template.posts.domain.repository.PostsRepository
import com.adriandeleon.kmp.template.posts.domain.usecase.GetPostsUseCase
import com.adriandeleon.kmp.template.posts.presentation.DefaultPostsComponent
import com.adriandeleon.kmp.template.posts.presentation.mapper.PostsUiMapper
import com.adriandeleon.kmp.template.posts.presentation.store.PostsStoreFactory
import com.arkivanov.decompose.ComponentContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val postsModule = module {
    factoryOf(::DefaultPostsRemoteDataSource) bind PostsRemoteDataSource::class
    factoryOf(::DefaultPostsLocalDataSource) bind PostsLocalDataSource::class
    factoryOf(::PostEntityMapper)
    factoryOf(::PostsUiMapper)
    factoryOf(::PostsStoreFactory)
    factoryOf(::DefaultPostsRepository) bind PostsRepository::class
    factoryOf(::GetPostsUseCase)
    factory<PostsComponent> { (componentContext: ComponentContext) ->
        DefaultPostsComponent(
            componentContext = componentContext,
            storeFactory = get(),
            uiMapper = get(),
        )
    }
}
