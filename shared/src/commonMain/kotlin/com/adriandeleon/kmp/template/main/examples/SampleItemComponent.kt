package com.adriandeleon.kmp.template.main.examples

import com.arkivanov.decompose.value.Value

/** Independent row component used by the Child Items example. */
interface SampleItemComponent {

    val state: Value<State>

    fun increment()

    data class State(
        val id: String,
        val title: String,
        val count: Int,
    )
}
