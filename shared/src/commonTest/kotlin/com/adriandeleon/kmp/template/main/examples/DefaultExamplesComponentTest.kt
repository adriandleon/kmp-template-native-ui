package com.adriandeleon.kmp.template.main.examples

import com.adriandeleon.kmp.template.common.util.testComponentContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class DefaultExamplesComponentTest :
    FunSpec({
        test("starts on list with sample child items") {
            val component = examplesComponent()

            component.activeChild().shouldBeInstanceOf<ExamplesComponent.Child.List>()
            component.state.value.itemIds shouldContainExactly
                listOf("sample-1", "sample-2", "sample-3")
            component.state.value.selectedItemId shouldBe "sample-1"
        }

        test("opens detail and pops back to list") {
            val component = examplesComponent()

            component.openDetail("sample-2")

            val detail =
                component.activeChild().shouldBeInstanceOf<ExamplesComponent.Child.Detail>()
            detail.component.itemId shouldBe "sample-2"
            component.state.value.selectedItemId shouldBe "sample-2"

            component.back()

            component.activeChild().shouldBeInstanceOf<ExamplesComponent.Child.List>()
        }

        test("back to index keeps the selected stack prefix") {
            val component = examplesComponent()

            component.openDetail("sample-1")
            component.openDetail("sample-2")
            component.backTo(0)

            component.activeChild().shouldBeInstanceOf<ExamplesComponent.Child.List>()
        }

        test("confirmation modal can be shown and dismissed") {
            val component = examplesComponent()

            component.showConfirmation()

            component.modalSlot.value.child
                ?.instance
                .shouldBeInstanceOf<ExamplesComponent.ModalChild.Confirmation>()

            component.dismissConfirmation()

            component.modalSlot.value.child shouldBe null
        }

        test("add item appends and selects a new child item") {
            val component = examplesComponent()

            component.addItem()

            component.state.value.itemIds shouldContainExactly
                listOf("sample-1", "sample-2", "sample-3", "sample-4")
            component.state.value.selectedItemId shouldBe "sample-4"
        }

        test("remove item deletes it and clears selection when needed") {
            val component = examplesComponent()

            component.selectItem("sample-2")
            component.removeItem("sample-2")

            component.state.value.itemIds shouldContainExactly listOf("sample-1", "sample-3")
            component.state.value.selectedItemId shouldBe null
        }

        test("child items keep independent state") {
            val component = examplesComponent()

            component.incrementItem("sample-1")
            component.incrementItem("sample-1")
            component.incrementItem("sample-2")

            component.itemComponent("sample-1").state.value.count shouldBe 2
            component.itemComponent("sample-2").state.value.count shouldBe 1
            component.itemComponent("sample-3").state.value.count shouldBe 0
        }
    })

private fun examplesComponent(): ExamplesComponent =
    DefaultExamplesComponent(testComponentContext())

private fun ExamplesComponent.activeChild(): ExamplesComponent.Child = stack.value.active.instance
