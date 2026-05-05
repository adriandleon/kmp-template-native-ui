package com.adriandeleon.kmp.template.main.examples

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.items.ChildItems
import com.arkivanov.decompose.router.items.Items
import com.arkivanov.decompose.router.items.LazyChildItems
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

@OptIn(ExperimentalDecomposeApi::class)
class PreviewExamplesComponent : ExamplesComponent {

    private val componentsById = mutableMapOf<String, PreviewSampleItemComponent>()

    private val mutableState =
        MutableValue(
            ExamplesComponent.State(
                itemIds = listOf("sample-1", "sample-2", "sample-3"),
                selectedItemId = "sample-1",
                nextItemNumber = 4,
            )
        )
    override val state: Value<ExamplesComponent.State> = mutableState

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

    override val childItems: LazyChildItems<ExamplesComponent.ItemConfig, SampleItemComponent> =
        PreviewLazyChildItems(this)

    override fun openDetail(itemId: String) {
        selectItem(itemId)
        mutableStack.value =
            ChildStack(
                active =
                    Child.Created<Any, ExamplesComponent.Child>(
                        configuration = itemId,
                        instance =
                            ExamplesComponent.Child.Detail(PreviewDetailComponent(itemId)),
                    ),
                backStack = mutableStack.value.items,
            )
    }

    override fun back() {
        backTo(mutableStack.value.items.lastIndex - 1)
    }

    override fun backTo(index: Int) {
        val selectedItems = mutableStack.value.items.take((index + 1).coerceIn(1, mutableStack.value.items.size))
        mutableStack.value = ChildStack(active = selectedItems.last(), backStack = selectedItems.dropLast(1))
    }

    override fun showConfirmation() {
        mutableModalSlot.value =
            ChildSlot(
                Child.Created<Any, ExamplesComponent.ModalChild>(
                    configuration = "confirmation",
                    instance = ExamplesComponent.ModalChild.Confirmation(PreviewConfirmationComponent),
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
    }

    override fun selectItem(itemId: String) {
        mutableState.value = mutableState.value.copy(selectedItemId = itemId)
    }

    override fun incrementItem(itemId: String) {
        itemComponent(itemId).increment()
    }

    override fun itemComponent(itemId: String): SampleItemComponent =
        componentsById.getOrPut(itemId) { PreviewSampleItemComponent(itemId) }

    private object PreviewListComponent : ExamplesComponent.ListComponent

    private class PreviewDetailComponent(override val itemId: String) :
        ExamplesComponent.DetailComponent

    private object PreviewConfirmationComponent : ExamplesComponent.ConfirmationComponent

    private class PreviewSampleItemComponent(itemId: String) : SampleItemComponent {
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

    private class PreviewLazyChildItems(
        private val component: PreviewExamplesComponent
    ) : LazyChildItems<ExamplesComponent.ItemConfig, SampleItemComponent>() {
        override val value: ChildItems<ExamplesComponent.ItemConfig, SampleItemComponent>
            get() = ChildItems(component.state.value.itemIds.map { ExamplesComponent.ItemConfig(it) })

        override fun subscribe(observer: (ChildItems<ExamplesComponent.ItemConfig, SampleItemComponent>) -> Unit) =
            component.state.subscribe { observer(value) }

        override fun get(configuration: ExamplesComponent.ItemConfig): SampleItemComponent =
            component.itemComponent(configuration.id)

        override fun navigate(
            transformer: (Items<ExamplesComponent.ItemConfig>) -> Items<ExamplesComponent.ItemConfig>,
            onComplete:
                (
                    Items<ExamplesComponent.ItemConfig>,
                    Items<ExamplesComponent.ItemConfig>,
                ) -> Unit,
        ) = Unit
    }
}
