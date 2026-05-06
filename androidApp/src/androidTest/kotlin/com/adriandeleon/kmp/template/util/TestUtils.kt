package com.adriandeleon.kmp.template.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.test.platform.app.InstrumentationRegistry

private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext

fun SemanticsNodeInteractionsProvider.onNodeWithTag(
    @StringRes resId: Int,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction =
    onNode(matcher = hasTestTag(context.getString(resId)), useUnmergedTree = useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onAllNodesWithTag(
    @StringRes resId: Int,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteractionCollection =
    onAllNodes(matcher = hasTestTag(context.getString(resId)), useUnmergedTree = useUnmergedTree)

fun SemanticsNodeInteractionsProvider.onNodeWithText(
    @StringRes resId: Int,
    substring: Boolean = false,
    ignoreCase: Boolean = false,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction =
    onNode(
        matcher = hasText(context.getString(resId), substring, ignoreCase),
        useUnmergedTree = useUnmergedTree,
    )

fun SemanticsNodeInteractionsProvider.onNodeWithContentDescription(
    @StringRes resId: Int,
    useUnmergedTree: Boolean = false,
): SemanticsNodeInteraction =
    onNode(matcher = hasContentDescription(context.getString(resId)), useUnmergedTree = useUnmergedTree)

fun textMatcher(
    @StringRes resId: Int,
    substring: Boolean = false,
    ignoreCase: Boolean = false,
): SemanticsMatcher = hasText(context.getString(resId), substring, ignoreCase)
