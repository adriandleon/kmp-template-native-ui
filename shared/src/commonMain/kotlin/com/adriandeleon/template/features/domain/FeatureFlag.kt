package com.adriandeleon.template.features.domain

internal interface FeatureFlag {
    val key: String
    val default: Boolean
}
