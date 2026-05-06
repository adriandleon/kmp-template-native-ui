package com.adriandeleon.kmp.template.konsist

import com.lemonappdev.konsist.api.verify.assertFalse
import kotlin.test.Test

class PlatformBoundaryTest {
    @Test
    fun `android production code does not import shared internals`() {
        val forbiddenImports =
            listOf(
                Regex("${PACKAGE_NAME.toRegexPattern()}\\..*\\.data\\..*"),
                Regex("${PACKAGE_NAME.toRegexPattern()}\\..*\\.domain\\..*"),
                Regex("${PACKAGE_NAME.toRegexPattern()}\\..*\\.presentation\\.store\\..*"),
                Regex("${PACKAGE_NAME.toRegexPattern()}\\..*\\.presentation\\.mapper\\..*"),
                Regex("${PACKAGE_NAME.toRegexPattern()}\\..*\\.Default.*"),
            )

        androidProductionScope.imports.assertFalse { import ->
            forbiddenImports.any { pattern -> import.hasNameMatching(pattern) }
        }
    }

    @Test
    fun `swift production code does not reference shared internals`() {
        val forbiddenSnippets =
            listOf(
                "DefaultRootComponent",
                "DefaultAuthComponent",
                "DefaultMainComponent",
                "DefaultExamplesComponent",
                "DefaultPostsComponent",
                "PostsStore",
                "PostsRepository",
                "DataSource",
            )

        projectScope
            .files
            .filter { it.path.contains("/iosApp/KMP-Template/") && it.path.endsWith(".swift") }
            .assertFalse { file ->
                forbiddenSnippets.any { snippet -> file.text.contains(snippet) }
            }
    }
}

private fun String.toRegexPattern(): String = replace(".", "\\.")
