package com.adriandeleon.kmp.template.konsist

import com.lemonappdev.konsist.api.Konsist

internal const val PACKAGE_NAME = "com.adriandeleon.kmp.template"

internal val projectScope = Konsist.scopeFromProject()

internal val productionScope = Konsist.scopeFromProduction()

internal val sharedProductionScope = Konsist.scopeFromProduction("shared")

internal val androidProductionScope = Konsist.scopeFromProduction("androidApp")
