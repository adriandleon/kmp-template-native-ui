package com.adriandeleon.kmp.template.konsist

import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.ext.list.modifierprovider.withoutModifier
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.ext.list.withNameStartingWith
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.test.Test

class SharedApiBoundaryTest {
    @Test
    fun `default components are internal implementation details`() {
        sharedProductionScope
            .classes(includeNested = false)
            .withNameStartingWith("Default")
            .withNameEndingWith("Component")
            .assertTrue { it.hasInternalModifier }
    }

    @Test
    fun `preview components are public test doubles for native previews and UI tests`() {
        sharedProductionScope
            .classes(includeNested = false)
            .withNameStartingWith("Preview")
            .withNameEndingWith("Component")
            .assertTrue { it.hasPublicOrDefaultModifier }
    }

    @Test
    fun `presentation store types are internal`() {
        sharedProductionScope
            .classesAndInterfacesAndObjects(includeNested = false)
            .withPackage("$PACKAGE_NAME..presentation.store..")
            .assertTrue { it.hasInternalModifier }
    }

    @Test
    fun `presentation mappers are internal`() {
        sharedProductionScope
            .classesAndInterfacesAndObjects(includeNested = false)
            .withPackage("$PACKAGE_NAME..presentation.mapper..")
            .assertTrue { it.hasInternalModifier }

        sharedProductionScope
            .functions()
            .withPackage("$PACKAGE_NAME..presentation.mapper..")
            .assertFalse { it.hasPublicOrDefaultModifier }
    }

    @Test
    fun `shared data layer types are internal`() {
        sharedProductionScope
            .classesAndInterfacesAndObjects(includeNested = false)
            .withPackage("$PACKAGE_NAME..data..")
            .filter { it.name != "Companion" }
            .assertTrue { it.hasInternalModifier }
    }

    @Test
    fun `public component interfaces expose UiState not State for renderable state`() {
        sharedProductionScope
            .interfaces()
            .withNameEndingWith("Component")
            .withoutModifier(KoModifier.INTERNAL)
            .assertTrue { component ->
                component.properties().none { property ->
                    property.name == "state" &&
                        property.type?.sourceType?.contains("Value<") == true
                }
            }
    }
}
