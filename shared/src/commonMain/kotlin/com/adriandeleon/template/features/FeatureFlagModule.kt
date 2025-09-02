package com.adriandeleon.template.features

import com.adriandeleon.template.BuildKonfig
import com.adriandeleon.template.common.util.DispatcherProvider
import com.adriandeleon.template.features.data.datasource.FeaturesDataSource
import com.adriandeleon.template.features.data.provider.ConfigCatProvider
import com.adriandeleon.template.features.domain.provider.FeatureFlagProvider
import com.adriandeleon.template.features.domain.repository.FeaturesRepository
import com.configcat.ConfigCatClient
import com.configcat.lazyLoad
import com.configcat.log.LogLevel
import com.configcat.log.Logger
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val featureFlagModule = module {
    singleOf(::firebaseRemoteConfig) { bind<FirebaseRemoteConfig>() }
    factoryOf(::FeaturesDataSource) { bind<FeaturesRepository>() }

    singleOf(::configCatClient)

    /**
     * Use only one provider at a time.
     *
     * Example:
     * - To use ConfigCat as a feature flag provider:
     * ```kotlin
     * single<FeatureFlagProvider> { ConfigCatProvider(get<ConfigCatClient>())
     * ```
     * - To use Firebase Remote Config as a feature flag provider:
     * ```kotlin
     * single<FeatureFlagProvider> { RemoteConfigProvider(get<FirebaseRemoteConfig>())
     * ```
     *
     * Or you can use a different provider depending on the environment:
     * ```kotlin
     * single<FeatureFlagProvider> {
     *   when {
     *     BuildKonfig.DEBUG -> RemoteConfigProvider(get<FirebaseRemoteConfig>())
     *     else -> ConfigCatProvider(get<ConfigCatClient>())
     *   }
     * }
     * ```
     */
    single<FeatureFlagProvider> { ConfigCatProvider(get<ConfigCatClient>()) }
}

private fun configCatClient(kermitLogger: Logger): ConfigCatClient =
    ConfigCatClient(sdkKey = BuildKonfig.CONFIGCAT_KEY) {
        logLevel = if (BuildKonfig.DEBUG) LogLevel.INFO else LogLevel.OFF
        pollingMode = lazyLoad { cacheRefreshInterval = 5.minutes }
        logger = kermitLogger
    }

private fun firebaseRemoteConfig(dispatcher: DispatcherProvider): FirebaseRemoteConfig {
    val config = Firebase.remoteConfig
    CoroutineScope(dispatcher.default).launch {
        config.settings { minimumFetchInterval = if (BuildKonfig.DEBUG) 5.minutes else 1.minutes }
        config.fetchAndActivate()
    }
    return config
}
