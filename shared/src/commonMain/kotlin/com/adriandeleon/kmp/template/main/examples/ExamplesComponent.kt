package com.adriandeleon.kmp.template.main.examples

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.items.LazyChildItems
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
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

    val workspace: Value<WorkspaceState>

    val panels:
        Value<
            ChildPanels<
                PanelMainConfig,
                PanelMainComponent,
                PanelDetailsConfig,
                PanelDetailsComponent,
                PanelExtraConfig,
                PanelExtraComponent,
            >
        >

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

    fun openPanelDetails(itemId: String)

    fun dismissPanelDetails()

    fun openPanelExtra(itemId: String)

    fun dismissPanelExtra()

    fun setPanelsMode(mode: ChildPanelsMode)

    fun activateWorkspacePane(paneId: String)

    fun addWorkspacePane()

    fun closeWorkspacePane(paneId: String)

    fun handleDeepLink(url: String): Boolean

    data class State(
        val itemIds: List<String>,
        val selectedItemId: String?,
        val nextItemNumber: Int,
        val panelItemId: String?,
        val panelsMode: ChildPanelsMode,
        val hasPanelDetails: Boolean,
        val hasPanelExtra: Boolean,
        val workspacePaneIds: List<String>,
        val activeWorkspacePaneId: String?,
        val nextWorkspacePaneNumber: Int,
        val lastDeepLinkPath: String?,
        val lastDeepLinkHandled: Boolean?,
    )

    data class WorkspaceState(
        val paneIds: List<String>,
        val activePaneId: String?,
        val activePaneTitle: String?,
    )

    @Serializable data class ItemConfig(val id: String)

    @Serializable data object PanelMainConfig

    @Serializable data class PanelDetailsConfig(val itemId: String)

    @Serializable data class PanelExtraConfig(val itemId: String)

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

    interface PanelMainComponent

    interface PanelDetailsComponent {
        val itemId: String
    }

    interface PanelExtraComponent {
        val itemId: String
    }

    interface WorkspacePaneComponent {
        val paneId: String

        val title: String
    }
}
