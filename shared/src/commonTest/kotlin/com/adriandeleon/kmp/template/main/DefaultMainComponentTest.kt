package com.adriandeleon.kmp.template.main

import com.adriandeleon.kmp.template.common.util.testComponentContext
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class DefaultMainComponentTest :
    FunSpec({
        test("starts on home with all tabs configured") {
            val component = mainComponent()

            component.pageOrder() shouldContainExactly
                listOf(
                    MainComponent.Page.Home,
                    MainComponent.Page.Examples,
                    MainComponent.Page.Adaptive,
                    MainComponent.Page.Settings,
                )
            component.uiState.value.selectedPage shouldBe MainComponent.Page.Home
            component.uiState.value.selectedIndex shouldBe 0
        }

        test("select page by page updates selected tab") {
            val component = mainComponent()

            component.selectPage(MainComponent.Page.Settings)

            component.uiState.value.selectedPage shouldBe MainComponent.Page.Settings
            component.pages.value.selectedIndex shouldBe 3
        }

        test("select page by index updates selected tab") {
            val component = mainComponent()

            component.selectPage(2)

            component.uiState.value.selectedPage shouldBe MainComponent.Page.Adaptive
            component.pages.value.selectedIndex shouldBe 2
        }

        test("select page coerces out of range indexes") {
            val component = mainComponent()

            component.selectPage(99)

            component.uiState.value.selectedPage shouldBe MainComponent.Page.Settings
            component.pages.value.selectedIndex shouldBe 3

            component.selectPage(-1)

            component.uiState.value.selectedPage shouldBe MainComponent.Page.Home
            component.pages.value.selectedIndex shouldBe 0
        }

        test("examples component remains available when another tab is selected") {
            val component = mainComponent()

            component.selectPage(MainComponent.Page.Settings)

            shouldNotThrowAny { component.examples } shouldNotBe null
        }
    })

private fun mainComponent(): MainComponent = DefaultMainComponent(testComponentContext())

private fun MainComponent.pageOrder(): List<MainComponent.Page> =
    pages.value.items.map { it.configuration }
