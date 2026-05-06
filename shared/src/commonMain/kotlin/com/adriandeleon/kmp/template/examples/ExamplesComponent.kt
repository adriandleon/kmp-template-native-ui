package com.adriandeleon.kmp.template.examples

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

/** Native UI contract for the neutral navigation examples. */
@Suppress("TooManyFunctions")
interface ExamplesComponent {

    val stack: Value<ChildStack<*, Child>>

    val modalSlot: Value<ChildSlot<*, ModalChild>>

    val uiState: Value<UiState>

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

    fun openPanelDetails(itemId: String)

    fun dismissPanelDetails()

    fun openPanelExtra(itemId: String)

    fun dismissPanelExtra()

    fun setPanelsMode(mode: PanelMode)

    fun activateWorkspacePane(paneId: String)

    fun addWorkspacePane()

    fun closeWorkspacePane(paneId: String)

    fun handleDeepLink(url: String): Boolean

    data class UiState(
        val itemIds: List<String>,
        val selectedItemId: String?,
        val nextItemNumber: Int,
        val panelItemId: String?,
        val panelsMode: PanelMode,
        val hasPanelDetails: Boolean,
        val hasPanelExtra: Boolean,
        val workspacePaneIds: List<String>,
        val activeWorkspacePaneId: String?,
        val nextWorkspacePaneNumber: Int,
        val lastDeepLinkPath: String?,
        val lastDeepLinkHandled: Boolean?,
    )

    enum class PanelMode {
        SINGLE,
        DUAL,
        TRIPLE,
    }

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
