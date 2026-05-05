package com.adriandeleon.kmp.template.main.examples

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.items.LazyChildItems
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

/** Nested showcase for Child Stack, Child Slot, and Child Items. */
@OptIn(ExperimentalDecomposeApi::class)
interface ExamplesComponent {

    val stack: Value<ChildStack<*, Child>>

    val modalSlot: Value<ChildSlot<*, ModalChild>>

    val childItems: LazyChildItems<ItemConfig, SampleItemComponent>

    val state: Value<State>

    fun openDetail(itemId: String)

    fun back()

    fun backTo(index: Int)

    fun showConfirmation()

    fun dismissConfirmation()

    fun addItem()

    fun removeItem(itemId: String)

    fun selectItem(itemId: String)

    fun incrementItem(itemId: String)

    fun itemComponent(itemId: String): SampleItemComponent

    data class State(
        val itemIds: List<String>,
        val selectedItemId: String?,
        val nextItemNumber: Int,
    )

    @Serializable data class ItemConfig(val id: String)

    sealed interface Child {
        data class List(val component: ListComponent) : Child

        data class Detail(val component: DetailComponent) : Child
    }

    sealed interface ModalChild {
        data class Confirmation(val component: ConfirmationComponent) : ModalChild
    }

    interface ListComponent

    interface DetailComponent {
        val itemId: String
    }

    interface ConfirmationComponent
}
