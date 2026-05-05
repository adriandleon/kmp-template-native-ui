package com.adriandeleon.kmp.template.main.examples

import com.adriandeleon.kmp.template.common.util.testComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

@OptIn(ExperimentalDecomposeApi::class)
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

        test("panels start with main only") {
            val component = examplesComponent()

            component.panels.value.main.instance.shouldBeInstanceOf<
                ExamplesComponent.PanelMainComponent
            >()
            component.panels.value.details shouldBe null
            component.panels.value.extra shouldBe null
            component.panels.value.mode shouldBe ChildPanelsMode.SINGLE
        }

        test("panels open details and extra for an item") {
            val component = examplesComponent()

            component.openPanelDetails("sample-2")
            component.openPanelExtra("sample-2")

            component.panels.value.details
                ?.instance
                .shouldBeInstanceOf<ExamplesComponent.PanelDetailsComponent>()
                .itemId shouldBe "sample-2"
            component.panels.value.extra
                ?.instance
                .shouldBeInstanceOf<ExamplesComponent.PanelExtraComponent>()
                .itemId shouldBe "sample-2"
            component.state.value.panelItemId shouldBe "sample-2"
        }

        test("panels can dismiss extra and details independently") {
            val component = examplesComponent()

            component.openPanelDetails("sample-2")
            component.openPanelExtra("sample-2")
            component.dismissPanelExtra()

            component.panels.value.details
                ?.instance
                .shouldBeInstanceOf<ExamplesComponent.PanelDetailsComponent>()
            component.panels.value.extra shouldBe null

            component.dismissPanelDetails()

            component.panels.value.details shouldBe null
            component.state.value.panelItemId shouldBe null
        }

        test("panels mode can be changed") {
            val component = examplesComponent()

            component.setPanelsMode(ChildPanelsMode.TRIPLE)

            component.panels.value.mode shouldBe ChildPanelsMode.TRIPLE
            component.state.value.panelsMode shouldBe ChildPanelsMode.TRIPLE
        }

        test("generic workspace starts with one active pane") {
            val component = examplesComponent()

            component.workspace.value.paneIds shouldContainExactly listOf("pane-1", "pane-2")
            component.workspace.value.activePaneId shouldBe "pane-1"
            component.workspace.value.activePaneTitle shouldBe "Pane 1"
        }

        test("generic workspace can add activate and close panes") {
            val component = examplesComponent()

            component.addWorkspacePane()
            component.activateWorkspacePane("pane-2")
            component.closeWorkspacePane("pane-1")

            component.workspace.value.paneIds shouldContainExactly listOf("pane-2", "pane-3")
            component.workspace.value.activePaneId shouldBe "pane-2"
            component.state.value.workspacePaneIds shouldContainExactly listOf("pane-2", "pane-3")
        }

        test("deep link opens an item detail") {
            val component = examplesComponent()

            component.handleDeepLink("template://examples/item/sample-3") shouldBe true

            val detail =
                component.activeChild().shouldBeInstanceOf<ExamplesComponent.Child.Detail>()
            detail.component.itemId shouldBe "sample-3"
            component.state.value.lastDeepLinkPath shouldBe "examples/item/sample-3"
            component.state.value.lastDeepLinkHandled shouldBe true
        }

        test("deep link can target panels and generic workspace") {
            val component = examplesComponent()

            component.handleDeepLink("template://examples/panel/sample-2") shouldBe true
            component.handleDeepLink("template://examples/workspace/pane-2") shouldBe true

            component.panels.value.details
                ?.instance
                .shouldBeInstanceOf<ExamplesComponent.PanelDetailsComponent>()
                .itemId shouldBe "sample-2"
            component.workspace.value.activePaneId shouldBe "pane-2"
        }

        test("unknown deep link is reported but ignored") {
            val component = examplesComponent()

            component.handleDeepLink("template://examples/unknown") shouldBe false

            component.state.value.lastDeepLinkPath shouldBe "examples/unknown"
            component.state.value.lastDeepLinkHandled shouldBe false
        }
    })

private fun examplesComponent(): ExamplesComponent =
    DefaultExamplesComponent(testComponentContext())

private fun ExamplesComponent.activeChild(): ExamplesComponent.Child = stack.value.active.instance
