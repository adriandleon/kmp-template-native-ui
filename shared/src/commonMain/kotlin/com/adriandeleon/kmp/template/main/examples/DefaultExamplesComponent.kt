package com.adriandeleon.kmp.template.main.examples

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.items.Items
import com.arkivanov.decompose.router.items.Items.ActiveLifecycleState
import com.arkivanov.decompose.router.items.ItemsNavigation
import com.arkivanov.decompose.router.items.childItems
import com.arkivanov.decompose.router.items.setItems
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.childPanels
import com.arkivanov.decompose.router.panels.dismissExtra
import com.arkivanov.decompose.router.panels.navigate
import com.arkivanov.decompose.router.panels.setMode
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.popTo
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

@OptIn(ExperimentalDecomposeApi::class)
class DefaultExamplesComponent(componentContext: ComponentContext) :
    ExamplesComponent, ComponentContext by componentContext {

    private val stackNavigation = StackNavigation<Configuration>()
    private val modalNavigation = SlotNavigation<ModalConfiguration>()
    private val itemsNavigation = ItemsNavigation<ExamplesComponent.ItemConfig>()
    private val panelsNavigation =
        PanelsNavigation<
            ExamplesComponent.PanelMainConfig,
            ExamplesComponent.PanelDetailsConfig,
            ExamplesComponent.PanelExtraConfig,
        >()

    private val mutableState =
        MutableValue(
            ExamplesComponent.State(
                itemIds = initialItemIds,
                selectedItemId = initialItemIds.firstOrNull(),
                nextItemNumber = initialItemIds.size + 1,
                panelItemId = null,
                panelsMode = ChildPanelsMode.SINGLE,
                hasPanelDetails = false,
                hasPanelExtra = false,
            )
        )
    override val state: Value<ExamplesComponent.State> = mutableState

    override val stack: Value<ChildStack<*, ExamplesComponent.Child>> =
        childStack(
            source = stackNavigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.List,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    override val modalSlot: Value<ChildSlot<*, ExamplesComponent.ModalChild>> =
        childSlot(
            source = modalNavigation,
            serializer = ModalConfiguration.serializer(),
            handleBackButton = true,
            childFactory = ::createModalChild,
        )

    override val childItems =
        childItems(
            source = itemsNavigation,
            serializer = ExamplesComponent.ItemConfig.serializer(),
            initialItems = {
                Items(
                    items = initialItemIds.map(::itemConfig),
                    activeItems =
                        initialItemIds.associate { id ->
                            itemConfig(id) to ActiveLifecycleState.STARTED
                        },
                )
            },
            childFactory = ::createSampleItemComponent,
        )

    override val panels:
        Value<
            ChildPanels<
                ExamplesComponent.PanelMainConfig,
                ExamplesComponent.PanelMainComponent,
                ExamplesComponent.PanelDetailsConfig,
                ExamplesComponent.PanelDetailsComponent,
                ExamplesComponent.PanelExtraConfig,
                ExamplesComponent.PanelExtraComponent,
            >
        > =
        childPanels(
            source = panelsNavigation,
            serializers =
                Triple(
                    ExamplesComponent.PanelMainConfig.serializer(),
                    ExamplesComponent.PanelDetailsConfig.serializer(),
                    ExamplesComponent.PanelExtraConfig.serializer(),
                ),
            initialPanels = {
                Panels(main = ExamplesComponent.PanelMainConfig, mode = ChildPanelsMode.SINGLE)
            },
            handleBackButton = true,
            onStateChanged = { newState, _ ->
                mutableState.value =
                    mutableState.value.copy(
                        panelItemId = newState.extra?.itemId ?: newState.details?.itemId,
                        panelsMode = newState.mode,
                        hasPanelDetails = newState.details != null,
                        hasPanelExtra = newState.extra != null,
                    )
            },
            mainFactory = { _, context -> DefaultPanelMainComponent(context) },
            detailsFactory = { configuration, context ->
                DefaultPanelDetailsComponent(configuration.itemId, context)
            },
            extraFactory = { configuration, context ->
                DefaultPanelExtraComponent(configuration.itemId, context)
            },
        )

    override fun openDetail(itemId: String) {
        selectItem(itemId)
        stackNavigation.pushNew(Configuration.Detail(itemId))
    }

    override fun back() {
        stackNavigation.pop()
    }

    override fun backTo(index: Int) {
        stackNavigation.popTo(index.coerceAtLeast(0))
    }

    override fun showConfirmation() {
        modalNavigation.activate(ModalConfiguration.Confirmation)
    }

    override fun dismissConfirmation() {
        modalNavigation.dismiss()
    }

    override fun addItem() {
        val nextNumber = mutableState.value.nextItemNumber
        val itemId = "sample-$nextNumber"
        val updatedIds = mutableState.value.itemIds + itemId

        mutableState.value =
            mutableState.value.copy(
                itemIds = updatedIds,
                selectedItemId = itemId,
                nextItemNumber = nextNumber + 1,
            )
        itemsNavigation.setItems { updatedIds.map(::itemConfig) }
    }

    override fun removeItem(itemId: String) {
        val updatedIds = mutableState.value.itemIds.filterNot { it == itemId }
        mutableState.value =
            mutableState.value.copy(
                itemIds = updatedIds,
                selectedItemId = mutableState.value.selectedItemId?.takeIf { it in updatedIds },
            )
        itemsNavigation.setItems { updatedIds.map(::itemConfig) }

        if (mutableState.value.panelItemId == itemId) {
            panelsNavigation.navigate(details = null, extra = null)
        }
    }

    override fun selectItem(itemId: String) {
        if (itemId in mutableState.value.itemIds) {
            mutableState.value = mutableState.value.copy(selectedItemId = itemId)
        }
    }

    override fun incrementItem(itemId: String) {
        itemComponent(itemId).increment()
    }

    override fun itemComponent(itemId: String): SampleItemComponent = childItems[itemConfig(itemId)]

    override fun openPanelDetails(itemId: String) {
        if (itemId in mutableState.value.itemIds) {
            selectItem(itemId)
            panelsNavigation.navigate(
                details = ExamplesComponent.PanelDetailsConfig(itemId),
                extra = null,
            )
        }
    }

    override fun dismissPanelDetails() {
        panelsNavigation.navigate(details = null, extra = null)
    }

    override fun openPanelExtra(itemId: String) {
        if (itemId in mutableState.value.itemIds) {
            selectItem(itemId)
            panelsNavigation.navigate(
                details = ExamplesComponent.PanelDetailsConfig(itemId),
                extra = ExamplesComponent.PanelExtraConfig(itemId),
            )
        }
    }

    override fun dismissPanelExtra() {
        panelsNavigation.dismissExtra()
    }

    override fun setPanelsMode(mode: ChildPanelsMode) {
        panelsNavigation.setMode(mode)
    }

    private fun createChild(
        configuration: Configuration,
        context: ComponentContext,
    ): ExamplesComponent.Child =
        when (configuration) {
            Configuration.List -> ExamplesComponent.Child.List(DefaultListComponent(context))
            is Configuration.Detail ->
                ExamplesComponent.Child.Detail(
                    DefaultDetailComponent(configuration.itemId, context)
                )
        }

    private fun createModalChild(
        configuration: ModalConfiguration,
        context: ComponentContext,
    ): ExamplesComponent.ModalChild =
        when (configuration) {
            ModalConfiguration.Confirmation ->
                ExamplesComponent.ModalChild.Confirmation(DefaultConfirmationComponent(context))
        }

    private fun createSampleItemComponent(
        configuration: ExamplesComponent.ItemConfig,
        context: ComponentContext,
    ): SampleItemComponent =
        DefaultSampleItemComponent(itemId = configuration.id, componentContext = context)

    private fun itemConfig(itemId: String) = ExamplesComponent.ItemConfig(itemId)

    @Serializable
    private sealed interface Configuration {
        @Serializable data object List : Configuration

        @Serializable data class Detail(val itemId: String) : Configuration
    }

    @Serializable
    private sealed interface ModalConfiguration {
        @Serializable data object Confirmation : ModalConfiguration
    }

    private class DefaultListComponent(componentContext: ComponentContext) :
        ExamplesComponent.ListComponent, ComponentContext by componentContext

    private class DefaultDetailComponent(
        override val itemId: String,
        componentContext: ComponentContext,
    ) : ExamplesComponent.DetailComponent, ComponentContext by componentContext

    private class DefaultConfirmationComponent(componentContext: ComponentContext) :
        ExamplesComponent.ConfirmationComponent, ComponentContext by componentContext

    private class DefaultPanelMainComponent(componentContext: ComponentContext) :
        ExamplesComponent.PanelMainComponent, ComponentContext by componentContext

    private class DefaultPanelDetailsComponent(
        override val itemId: String,
        componentContext: ComponentContext,
    ) : ExamplesComponent.PanelDetailsComponent, ComponentContext by componentContext

    private class DefaultPanelExtraComponent(
        override val itemId: String,
        componentContext: ComponentContext,
    ) : ExamplesComponent.PanelExtraComponent, ComponentContext by componentContext

    private class DefaultSampleItemComponent(itemId: String, componentContext: ComponentContext) :
        SampleItemComponent, ComponentContext by componentContext {

        private val mutableState =
            MutableValue(
                SampleItemComponent.State(
                    id = itemId,
                    title = itemId.replaceFirstChar { it.uppercase() },
                    count = 0,
                )
            )
        override val state: Value<SampleItemComponent.State> = mutableState

        override fun increment() {
            mutableState.value = mutableState.value.copy(count = mutableState.value.count + 1)
        }
    }

    private companion object {
        val initialItemIds = listOf("sample-1", "sample-2", "sample-3")
    }
}
