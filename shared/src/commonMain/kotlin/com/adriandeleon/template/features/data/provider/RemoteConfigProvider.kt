package com.adriandeleon.template.features.data.provider

import com.adriandeleon.template.features.domain.provider.FeatureFlagProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.FirebaseAnalytics
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig

internal class RemoteConfigProvider(private val remoteConfig: FirebaseRemoteConfig) :
    FeatureFlagProvider {

    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        remoteConfig.getValue(key).asBoolean()

    override suspend fun getString(key: String, defaultValue: String): String =
        remoteConfig.getValue(key).asString()

    override suspend fun getInt(key: String, defaultValue: Int): Int =
        remoteConfig.getValue(key).asLong().toInt()

    override suspend fun getDouble(key: String, defaultValue: Double): Double =
        remoteConfig.getValue(key).asDouble()

    override suspend fun getAllKeys(): Collection<String> = remoteConfig.all.keys

    override suspend fun getAllValues(): Map<String, Any?> = remoteConfig.all

    override suspend fun refresh(): Boolean = remoteConfig.fetchAndActivate()

    override fun setUserProperties(userId: String, email: String?, country: String?) {
        firebaseAnalytics.setUserId(userId)
        email?.let { firebaseAnalytics.setUserProperty("email", it) }
        country?.let { firebaseAnalytics.setUserProperty("country", it) }
    }
}
