package com.adriandeleon.kmp.template.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.util.onNodeWithTag

fun ComposeContentTestRule.launchHomeView(component: PreviewHomeComponent): HomeViewRobot {
    setContent { HomeView(component) }
    return HomeViewRobot(this)
}

class HomeViewRobot(private val rule: ComposeContentTestRule) {
    infix fun verify(block: HomeViewVerification.() -> Unit): HomeViewVerification {
        rule.waitForIdle()
        return HomeViewVerification(rule).apply(block)
    }
}

class HomeViewVerification(private val rule: ComposeContentTestRule) {
    fun homeScreenIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_home_screen).assertIsDisplayed()
    }
}
