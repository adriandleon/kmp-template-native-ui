package com.adriandeleon.kmp.template.common

import com.adriandeleon.kmp.template.common.util.provideDispatcher
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val commonModule = module { factoryOf(::provideDispatcher) }
