package com.adriandeleon.template.features.domain

internal typealias Features = Map<String, Boolean>

internal fun Features.get(feature: FeatureFlag): Boolean =
    getOrElse(feature.key) { feature.default }

internal fun Map<FeatureFlag, Boolean>.asFeatures(): Features = mapKeys { it.key.key }
