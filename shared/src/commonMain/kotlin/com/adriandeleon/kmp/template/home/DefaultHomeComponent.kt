package com.adriandeleon.kmp.template.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

/**
 * Default implementation of [HomeComponent]
 *
 * @param componentContext Context of the component
 * @see HomeComponent
 */
internal class DefaultHomeComponent(componentContext: ComponentContext) :
    HomeComponent, ComponentContext by componentContext {

    override val uiState: Value<HomeComponent.UiState> = MutableValue(HomeComponent.UiState())
}
