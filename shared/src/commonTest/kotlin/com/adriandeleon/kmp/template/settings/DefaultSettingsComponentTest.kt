package com.adriandeleon.kmp.template.settings

import com.adriandeleon.kmp.template.common.util.testComponentContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DefaultSettingsComponentTest :
    FunSpec({
        test("exposes ready state") {
            val component = DefaultSettingsComponent(testComponentContext())

            component.uiState.value.isReady shouldBe true
        }
    })
