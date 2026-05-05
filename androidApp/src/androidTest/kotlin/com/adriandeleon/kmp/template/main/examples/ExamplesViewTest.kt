package com.adriandeleon.kmp.template.main.examples

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class ExamplesViewTest {
    @get:Rule val composeTestRule = createComposeRule()

    private val component = PreviewExamplesComponent()

    @Test
    fun verifyExamplesScreenDisplaysPreviewItems() {
        composeTestRule.launchExamplesView(component) verify {
            screenIsDisplayed()
            itemRowsMatchUiState()
        }
    }

    @Test
    fun verifyAddItemUpdatesPreviewComponentState() {
        composeTestRule.launchExamplesView(component) { tapAddItem() } verify {
            addedItemIsInComponentState()
            itemRowsMatchUiState()
        }
    }

    @Test
    fun verifyModalActionDisplaysConfirmation() {
        composeTestRule.launchExamplesView(component) { tapShowModal() } verify {
            confirmationModalIsDisplayed()
        }
    }
}
