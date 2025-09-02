package com.adriandeleon.template.features.data.datasource

import com.adriandeleon.template.features.domain.FeatureFlag
import com.adriandeleon.template.features.domain.Features
import com.adriandeleon.template.features.domain.asFeatures
import com.adriandeleon.template.features.domain.provider.FeatureFlagProvider
import com.adriandeleon.template.features.domain.repository.FeaturesRepository

internal class FeaturesDataSource(private val provider: FeatureFlagProvider) : FeaturesRepository {

    override suspend fun get(flag: FeatureFlag): Boolean {
        return provider.getBoolean(flag.key, defaultValue = flag.default)
    }

    override suspend fun get(vararg flags: FeatureFlag): Map<FeatureFlag, Boolean> {
        val result: MutableMap<FeatureFlag, Boolean> = mutableMapOf()
        flags.forEach { flag -> result[flag] = get(flag) }
        return result.toMap()
    }

    override suspend fun get(flags: List<FeatureFlag>): Features {
        val result: MutableMap<FeatureFlag, Boolean> = mutableMapOf()
        flags.forEach { flag -> result[flag] = get(flag) }
        return result.toMap().asFeatures()
    }

    override suspend fun setUserData(userId: String, email: String?, country: String?) {
        provider.setUserProperties(userId, email, country)
    }
}
