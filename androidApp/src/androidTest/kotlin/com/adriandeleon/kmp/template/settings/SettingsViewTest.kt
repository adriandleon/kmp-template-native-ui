package com.adriandeleon.kmp.template.settings

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class SettingsViewTest {
    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun verifySettingsScreenIsDisplayed() {
        composeTestRule.launchSettingsView(PreviewSettingsComponent()) verify {
            settingsScreenIsDisplayed()
        }
    }
}
