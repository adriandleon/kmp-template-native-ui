package com.adriandeleon.kmp.template.examples

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

@Suppress("TooManyFunctions")
class PreviewExamplesComponent : ExamplesComponent {

    private val componentsById = mutableMapOf<String, PreviewSampleItemComponent>()

    private val mutableState =
        MutableValue(
            ExamplesComponent.UiState(
                itemIds = listOf("sample-1", "sample-2", "sample-3"),
                selectedItemId = "sample-1",
                nextItemNumber = 4,
                panelItemId = null,
                panelsMode = ExamplesComponent.PanelMode.SINGLE,
                hasPanelDetails = false,
                hasPanelExtra = false,
                workspacePaneIds = listOf("pane-1", "pane-2"),
                activeWorkspacePaneId = "pane-1",
                nextWorkspacePaneNumber = 3,
                lastDeepLinkPath = null,
                lastDeepLinkHandled = null,
            )
        )
    override val uiState: Value<ExamplesComponent.UiState> = mutableState

    private val mutableStack =
        MutableValue(
            ChildStack<Any, ExamplesComponent.Child>(
                configuration = "list",
                instance = ExamplesComponent.Child.List(PreviewListComponent),
            )
        )
    override val stack: Value<ChildStack<*, ExamplesComponent.Child>> = mutableStack

    private val mutableModalSlot = MutableValue(ChildSlot<Any, ExamplesComponent.ModalChild>())
    override val modalSlot: Value<ChildSlot<*, ExamplesComponent.ModalChild>> = mutableModalSlot

    override fun openDetail(itemId: String) {
        selectItem(itemId)
        mutableStack.value =
            ChildStack(
                active =
                    Child.Created<Any, ExamplesComponent.Child>(
                        configuration = itemId,
                        instance = ExamplesComponent.Child.Detail(PreviewDetailComponent(itemId)),
                    ),
                backStack = mutableStack.value.items,
            )
    }

    override fun back() {
        backTo(mutableStack.value.items.lastIndex - 1)
    }

    override fun backTo(index: Int) {
        val selectedItems =
            mutableStack.value.items.take((index + 1).coerceIn(1, mutableStack.value.items.size))
        mutableStack.value =
            ChildStack(active = selectedItems.last(), backStack = selectedItems.dropLast(1))
    }

    override fun showConfirmation() {
        mutableModalSlot.value =
            ChildSlot(
                Child.Created<Any, ExamplesComponent.ModalChild>(
                    configuration = "confirmation",
                    instance =
                        ExamplesComponent.ModalChild.Confirmation(PreviewConfirmationComponent),
                )
            )
    }

    override fun dismissConfirmation() {
        mutableModalSlot.value = ChildSlot()
    }

    override fun addItem() {
        val nextNumber = mutableState.value.nextItemNumber
        val itemId = "sample-$nextNumber"
        mutableState.value =
            mutableState.value.copy(
                itemIds = mutableState.value.itemIds + itemId,
                selectedItemId = itemId,
                nextItemNumber = nextNumber + 1,
            )
    }

    override fun removeItem(itemId: String) {
        mutableState.value =
            mutableState.value.copy(
                itemIds = mutableState.value.itemIds.filterNot { it == itemId },
                selectedItemId = mutableState.value.selectedItemId?.takeIf { it != itemId },
            )
        if (mutableState.value.panelItemId == itemId) {
            updatePanels(detailsItemId = null, extraItemId = null)
        }
    }

    override fun selectItem(itemId: String) {
        mutableState.value = mutableState.value.copy(selectedItemId = itemId)
    }

    override fun incrementItem(itemId: String) {
        itemComponent(itemId).increment()
    }

    override fun itemComponent(itemId: String): SampleItemComponent =
        componentsById.getOrPut(itemId) { PreviewSampleItemComponent(itemId) }

    override fun openPanelDetails(itemId: String) {
        if (itemId in mutableState.value.itemIds) {
            selectItem(itemId)
            updatePanels(detailsItemId = itemId, extraItemId = null)
        }
    }

    override fun dismissPanelDetails() {
        updatePanels(detailsItemId = null, extraItemId = null)
    }

    override fun openPanelExtra(itemId: String) {
        if (itemId in mutableState.value.itemIds) {
            selectItem(itemId)
            updatePanels(detailsItemId = itemId, extraItemId = itemId)
        }
    }

    override fun dismissPanelExtra() {
        updatePanels(
            detailsItemId =
                mutableState.value.panelItemId.takeIf { mutableState.value.hasPanelDetails },
            extraItemId = null,
        )
    }

    override fun setPanelsMode(mode: ExamplesComponent.PanelMode) {
        mutableState.value = mutableState.value.copy(panelsMode = mode)
    }

    override fun activateWorkspacePane(paneId: String) {
        if (paneId in mutableState.value.workspacePaneIds) {
            updateWorkspace(
                paneIds = mutableState.value.workspacePaneIds,
                activePaneId = paneId,
                nextPaneNumber = mutableState.value.nextWorkspacePaneNumber,
            )
        }
    }

    override fun addWorkspacePane() {
        val paneId = "pane-${mutableState.value.nextWorkspacePaneNumber}"
        updateWorkspace(
            paneIds = mutableState.value.workspacePaneIds + paneId,
            activePaneId = paneId,
            nextPaneNumber = mutableState.value.nextWorkspacePaneNumber + 1,
        )
    }

    override fun closeWorkspacePane(paneId: String) {
        val currentPaneIds = mutableState.value.workspacePaneIds
        if (paneId in currentPaneIds && currentPaneIds.size > 1) {
            val updatedPaneIds = currentPaneIds.filterNot { it == paneId }
            updateWorkspace(
                paneIds = updatedPaneIds,
                activePaneId =
                    mutableState.value.activeWorkspacePaneId?.takeIf { it != paneId }
                        ?: updatedPaneIds.firstOrNull(),
                nextPaneNumber = mutableState.value.nextWorkspacePaneNumber,
            )
        }
    }

    override fun handleDeepLink(url: String): Boolean {
        val path = url.substringAfter("://", url).substringBefore("?").trim('/')
        val parts = path.split("/").filter(String::isNotBlank)
        val handled =
            when {
                parts.matchesDeepLink(ItemPath) -> {
                    openDetail(parts[ItemIdIndex])
                    true
                }
                parts.matchesDeepLink(PanelPath) -> {
                    openPanelDetails(parts[ItemIdIndex])
                    true
                }
                parts.matchesConfirmationDeepLink() -> {
                    showConfirmation()
                    true
                }
                parts.matchesDeepLink(WorkspacePath) -> {
                    activateWorkspacePane(parts[ItemIdIndex])
                    parts[ItemIdIndex] in mutableState.value.workspacePaneIds
                }
                else -> false
            }

        mutableState.value =
            mutableState.value.copy(lastDeepLinkPath = path, lastDeepLinkHandled = handled)

        return handled
    }

    private fun updateWorkspace(paneIds: List<String>, activePaneId: String?, nextPaneNumber: Int) {
        mutableState.value =
            mutableState.value.copy(
                workspacePaneIds = paneIds,
                activeWorkspacePaneId = activePaneId,
                nextWorkspacePaneNumber = nextPaneNumber,
            )
    }

    private fun updatePanels(detailsItemId: String?, extraItemId: String?) {
        mutableState.value =
            mutableState.value.copy(
                panelItemId = extraItemId ?: detailsItemId,
                hasPanelDetails = detailsItemId != null,
                hasPanelExtra = extraItemId != null,
            )
    }

    private fun List<String>.matchesDeepLink(action: String): Boolean =
        size == DeepLinkItemParts &&
            this[RootPathIndex] == ExamplesPath &&
            this[ActionPathIndex] == action

    private fun List<String>.matchesConfirmationDeepLink(): Boolean =
        size == DeepLinkConfirmationParts &&
            this[RootPathIndex] == ExamplesPath &&
            this[ActionPathIndex] == ConfirmationPath

    private object PreviewListComponent : ExamplesComponent.ListComponent

    private class PreviewDetailComponent(override val itemId: String) :
        ExamplesComponent.DetailComponent

    private object PreviewConfirmationComponent : ExamplesComponent.ConfirmationComponent

    private class PreviewSampleItemComponent(itemId: String) : SampleItemComponent {
        private val mutableState =
            MutableValue(
                SampleItemComponent.UiState(
                    id = itemId,
                    title = itemId.replaceFirstChar { it.uppercase() },
                    count = 0,
                )
            )
        override val uiState: Value<SampleItemComponent.UiState> = mutableState

        override fun increment() {
            mutableState.value = mutableState.value.copy(count = mutableState.value.count + 1)
        }
    }

    private companion object {
        const val ExamplesPath = "examples"
        const val ItemPath = "item"
        const val PanelPath = "panel"
        const val WorkspacePath = "workspace"
        const val ConfirmationPath = "confirmation"
        const val RootPathIndex = 0
        const val ActionPathIndex = 1
        const val ItemIdIndex = 2
        const val DeepLinkConfirmationParts = 2
        const val DeepLinkItemParts = 3
    }
}
