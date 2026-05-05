package com.adriandeleon.kmp.template.main.examples

import com.arkivanov.decompose.value.Value

/** Independent row component used by the Child Items example. */
interface SampleItemComponent {

    val uiState: Value<UiState>

    fun increment()

    data class UiState(val id: String, val title: String, val count: Int)
}
