package com.adriandeleon.kmp.template.home

import com.adriandeleon.kmp.template.common.util.testComponentContext
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DefaultHomeComponentTest :
    FunSpec({
        test("exposes ready state") {
            val component = DefaultHomeComponent(testComponentContext())
            component.uiState.value.isReady shouldBe true
        }
    })
