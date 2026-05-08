package com.adriandeleon.kmp.template.examples

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.util.onAllNodesWithTag
import com.adriandeleon.kmp.template.util.onNodeWithTag
import com.adriandeleon.kmp.template.util.onNodeWithText
import com.adriandeleon.kmp.template.util.textMatcher
import org.junit.Assert.assertEquals

fun ComposeContentTestRule.launchExamplesView(
    component: PreviewExamplesComponent,
    block: ExamplesViewRobot.() -> Unit = {},
): ExamplesViewRobot {
    setContent { ExamplesView(component) }
    return ExamplesViewRobot(this, component).apply(block)
}

class ExamplesViewRobot(
    private val rule: ComposeContentTestRule,
    private val component: PreviewExamplesComponent,
) {
    infix fun verify(block: ExamplesViewVerification.() -> Unit): ExamplesViewVerification {
        rule.waitForIdle()
        return ExamplesViewVerification(rule, component).apply(block)
    }

    fun tapAddItem() {
        rule.onNodeWithTag(R.string.tag_examples_add_button).performClick()
    }

    fun tapShowModal() {
        rule.onNodeWithTag(R.string.tag_examples_modal_button).performClick()
    }

    fun scrollToDeepLinkSection() {
        rule
            .onNodeWithTag(R.string.tag_examples_screen)
            .performScrollToNode(textMatcher(R.string.examples_deeplink_title))
    }
}

class ExamplesViewVerification(
    private val rule: ComposeContentTestRule,
    private val component: PreviewExamplesComponent,
) {
    fun screenIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_examples_screen).assertIsDisplayed()
    }

    fun itemRowsMatchUiState() {
        rule
            .onAllNodesWithTag(R.string.tag_examples_item)
            .assertCountEquals(component.uiState.value.itemIds.size)
    }

    fun addedItemIsInComponentState() {
        assertEquals("sample-4", component.uiState.value.selectedItemId)
    }

    fun confirmationModalIsDisplayed() {
        rule.onNodeWithText(R.string.examples_confirmation_title).assertIsDisplayed()
    }

    fun deepLinkSectionIsDisplayed() {
        rule.onNodeWithText(R.string.examples_deeplink_title).assertIsDisplayed()
    }
}
