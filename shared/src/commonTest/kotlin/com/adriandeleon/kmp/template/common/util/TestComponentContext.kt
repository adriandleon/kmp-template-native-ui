package com.adriandeleon.kmp.template.common.util

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume

internal fun testComponentContext() = DefaultComponentContext(
    lifecycle = LifecycleRegistry().also { it.resume() }
)
