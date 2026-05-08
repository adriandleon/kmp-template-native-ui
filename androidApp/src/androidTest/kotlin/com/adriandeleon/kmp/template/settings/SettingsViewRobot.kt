package com.adriandeleon.kmp.template.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.adriandeleon.kmp.template.R
import com.adriandeleon.kmp.template.util.onNodeWithTag

fun ComposeContentTestRule.launchSettingsView(
    component: PreviewSettingsComponent,
): SettingsViewRobot {
    setContent { SettingsView(component) }
    return SettingsViewRobot(this)
}

class SettingsViewRobot(private val rule: ComposeContentTestRule) {
    infix fun verify(block: SettingsViewVerification.() -> Unit): SettingsViewVerification {
        rule.waitForIdle()
        return SettingsViewVerification(rule).apply(block)
    }
}

class SettingsViewVerification(private val rule: ComposeContentTestRule) {
    fun settingsScreenIsDisplayed() {
        rule.onNodeWithTag(R.string.tag_settings_screen).assertIsDisplayed()
    }
}
