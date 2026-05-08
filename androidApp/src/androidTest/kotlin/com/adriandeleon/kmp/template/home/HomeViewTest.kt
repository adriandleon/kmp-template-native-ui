package com.adriandeleon.kmp.template.home

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class HomeViewTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun verifyHomeScreenIsDisplayed() {
        composeTestRule.launchHomeView(PreviewHomeComponent()) verify {
            homeScreenIsDisplayed()
        }
    }
}
