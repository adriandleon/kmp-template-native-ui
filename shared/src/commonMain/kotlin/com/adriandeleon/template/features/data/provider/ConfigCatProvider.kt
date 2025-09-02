package com.adriandeleon.template.features.data.provider

import com.adriandeleon.template.features.domain.provider.FeatureFlagProvider
import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.getValue

internal class ConfigCatProvider(private val configCatClient: ConfigCatClient) :
    FeatureFlagProvider {
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        configCatClient.getValue(key, defaultValue)

    override suspend fun getString(key: String, defaultValue: String): String =
        configCatClient.getValue(key, defaultValue)

    override suspend fun getInt(key: String, defaultValue: Int): Int =
        configCatClient.getValue(key, defaultValue)

    override suspend fun getDouble(key: String, defaultValue: Double): Double =
        configCatClient.getValue(key, defaultValue)

    override suspend fun getAllKeys(): Collection<String> = configCatClient.getAllKeys()

    override suspend fun getAllValues(): Map<String, Any?> = configCatClient.getAllValues()

    override suspend fun refresh(): Boolean = configCatClient.forceRefresh().isSuccess

    override fun setUserProperties(userId: String, email: String?, country: String?) {
        val user = ConfigCatUser(identifier = userId, email = email, country = country)
        configCatClient.setDefaultUser(user)
    }
}
