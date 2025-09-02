package com.adriandeleon.template.features.domain.repository

import com.adriandeleon.template.features.domain.FeatureFlag
import com.adriandeleon.template.features.domain.Features

internal interface FeaturesRepository {

    suspend fun get(flag: FeatureFlag): Boolean

    suspend fun get(vararg flags: FeatureFlag): Map<FeatureFlag, Boolean>

    suspend fun get(flags: List<FeatureFlag>): Features

    suspend fun setUserData(userId: String, email: String? = null, country: String? = null)
}
