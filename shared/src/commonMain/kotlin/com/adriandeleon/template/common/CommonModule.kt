package com.adriandeleon.template.common

import com.adriandeleon.template.common.util.provideDispatcher
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val commonModule = module { factoryOf(::provideDispatcher) }
