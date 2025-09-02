package com.adriandeleon.template.features.domain.provider

/**
 * Interface for feature flag providers that abstracts the implementation details of different
 * feature flag services (e.g., ConfigCat, Firebase RemoteConfig).
 */
internal interface FeatureFlagProvider {
    /**
     * Gets a boolean feature flag value.
     *
     * @param key The feature flag key
     * @param defaultValue The default value to return if the flag is not found
     * @return The feature flag value or the default value if not found
     */
    suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    /**
     * Gets a string feature flag value.
     *
     * @param key The feature flag key
     * @param defaultValue The default value to return if the flag is not found
     * @return The feature flag value or the default value if not found
     */
    suspend fun getString(key: String, defaultValue: String = ""): String

    /**
     * Gets an integer feature flag value.
     *
     * @param key The feature flag key
     * @param defaultValue The default value to return if the flag is not found
     * @return The feature flag value or the default value if not found
     */
    suspend fun getInt(key: String, defaultValue: Int = 0): Int

    /**
     * Gets a double feature flag value.
     *
     * @param key The feature flag key
     * @param defaultValue The default value to return if the flag is not found
     * @return The feature flag value or the default value if not found
     */
    suspend fun getDouble(key: String, defaultValue: Double = 0.0): Double

    /**
     * Gets all available feature flag keys.
     *
     * @return Collection of all available feature flag keys
     */
    suspend fun getAllKeys(): Collection<String>

    /**
     * Gets all feature flag values.
     *
     * @return Map of all feature flag keys and their values
     */
    suspend fun getAllValues(): Map<String, Any?>

    /**
     * Forces a refresh of the feature flags from the remote server.
     *
     * @return true if the refresh was successful, false otherwise
     */
    suspend fun refresh(): Boolean

    /**
     * Sets user properties for the feature flag provider.
     *
     * @param userId The user ID
     * @param email The user email
     * @param country The user country
     */
    fun setUserProperties(userId: String, email: String?, country: String?)
}
