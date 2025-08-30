package com.adriandeleon.template.common

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes

/**
 * Initializes Koin for the common module.
 *
 * @param config Optional KoinAppDeclaration for additional configuration.
 */
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        includes(config)
        modules(
            // Here the list of shared modules
        )
    }
}
