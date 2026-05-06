package com.adriandeleon.kmp.template.common.util

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf

internal fun testComponentContext() =
    DefaultComponentContext(lifecycle = LifecycleRegistry().also { it.resume() })

internal fun <T : Any> createComponentForTest(factory: (ComponentContext) -> T): T =
    factory(testComponentContext())

internal inline fun <reified T : Any> Value<ChildStack<*, *>>.assertActiveInstance() {
    value.assertActiveInstance<T>()
}

internal inline fun <reified T : Any> ChildStack<*, *>.assertActiveInstance() {
    active.instance.shouldBeTypeOf<T>()
}

internal inline fun <reified T : Any> Value<ChildStack<*, *>>.activeInstance(): T =
    value.active.instance as T

internal inline fun <reified T : Any> Value<ChildSlot<*, *>>.assertActiveSlotInstance() {
    value.assertActiveSlotInstance<T>()
}

internal inline fun <reified T : Any> ChildSlot<*, *>.assertActiveSlotInstance() {
    child?.instance.shouldBeTypeOf<T>()
}

internal inline fun <reified T : Any> Value<ChildSlot<*, *>>.activeSlotInstance(): T =
    value.child?.instance as T

internal fun Value<ChildSlot<*, *>>.assertEmptySlot() {
    value.child shouldBe null
}

internal inline fun <reified T : Any> Value<ChildPages<*, *>>.selectedPageInstance(): T =
    value.items[value.selectedIndex].instance as T

internal inline fun <reified T : Any> Value<ChildPages<*, *>>.assertSelectedPageInstance() {
    selectedPageInstance<T>().shouldBeTypeOf<T>()
}
