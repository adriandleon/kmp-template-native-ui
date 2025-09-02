# Feature Flags Integration Guide

This document covers the complete feature flags integration setup for the Kotlin Multiplatform project, including provider configuration, usage patterns, and best practices for managing feature toggles across Android and iOS platforms.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [🚩 What are Feature Flags?](#-what-are-feature-flags)
- [🏗️ Architecture](#-architecture)
- [⚙️ Setup](#-setup)
- [🔧 Configuration](#-configuration)
- [💻 Usage Examples](#-usage-examples)
- [🔄 Provider Switching](#-provider-switching)
- [🎯 User Targeting](#-user-targeting)
- [🧪 Testing](#-testing)
- [🚀 Production Deployment](#-production-deployment)
- [🐛 Troubleshooting](#-troubleshooting)
- [📚 Best Practices](#-best-practices)

## 🎯 Overview

The feature flags system provides a flexible, provider-agnostic way to manage feature toggles in your Kotlin Multiplatform application. It supports multiple providers (ConfigCat and Firebase Remote Config) and allows for easy switching between them without changing your application code.

## 🚩 What are Feature Flags?

Feature flags (also known as feature toggles) are a software development technique that allows you to:

- **Enable/disable features** without deploying new code
- **A/B test features** with different user segments
- **Gradually roll out features** to specific user groups
- **Quickly disable features** in case of issues
- **Manage configuration** remotely without app updates

## 🏗️ Architecture

### **Multiplatform Integration**

The feature flags system follows a clean architecture pattern:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Android UI    │  │    iOS UI       │  │  Shared UI  │ │
│  │  (Compose)      │  │   (SwiftUI)     │  │  (KMP)      │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                     │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   ViewModels    │  │   Components    │  │  Use Cases  │ │
│  │   (MVIKotlin)   │  │  (Decompose)    │  │  (Domain)   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │   Repository    │  │   Data Source   │  │  Providers  │ │
│  │   (Domain)      │  │   (Data)        │  │  (Data)     │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                   External Services                         │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │    ConfigCat    │  │ Firebase Remote │  │   Custom    │ │
│  │   (Provider)    │  │   Config        │  │  Provider   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### **Core Components**

1. **FeatureFlag Interface**: Type-safe feature flag definitions
2. **FeatureFlagProvider**: Abstract interface for different providers
3. **FeaturesRepository**: Repository pattern for feature flag access
4. **FeaturesDataSource**: Data source implementation
5. **Provider Implementations**: ConfigCat and Firebase Remote Config

## ⚙️ Setup

### **Dependencies**

The feature flags system is already configured in the project. The following dependencies are included:

```kotlin
// ConfigCat for feature flags
implementation("com.configcat:configcat-kotlin-client:5.1.0")

// Firebase Remote Config
implementation("dev.gitlive:firebase-config:2.3.0")
```

### **Module Registration**

The feature flags module is automatically registered in the Koin dependency injection container:

```kotlin
// In your Koin setup
startKoin {
    modules(featureFlagModule)
}
```

## 🔧 Configuration

### **Environment Variables**

Configure the following environment variables in your `local.properties` file:

```properties
# ConfigCat Configuration
CONFIGCAT_AND_TEST_KEY=your_configcat_test_key
CONFIGCAT_AND_LIVE_KEY=your_configcat_live_key
CONFIGCAT_IOS_TEST_KEY=your_configcat_ios_test_key
CONFIGCAT_IOS_LIVE_KEY=your_configcat_ios_live_key

# Firebase Configuration (if using Firebase Remote Config)
# Firebase configuration is handled through google-services.json and GoogleService-Info.plist
```

### **Provider Selection**

The system supports multiple providers. Configure your preferred provider in `FeatureFlagModule.kt`:

```kotlin
// Option 1: ConfigCat Provider
single<FeatureFlagProvider> { ConfigCatProvider(get<ConfigCatClient>()) }

// Option 2: Firebase Remote Config Provider
single<FeatureFlagProvider> { RemoteConfigProvider(get<FirebaseRemoteConfig>()) }

// Option 3: Environment-based selection
single<FeatureFlagProvider> {
    when {
        BuildKonfig.DEBUG -> RemoteConfigProvider(get<FirebaseRemoteConfig>())
        else -> ConfigCatProvider(get<ConfigCatClient>())
    }
}
```

## 💻 Usage Examples

### **Defining Feature Flags**

Create type-safe feature flag definitions:

```kotlin
// Define feature flags as objects implementing FeatureFlag
object NewUserOnboarding : FeatureFlag {
    override val key = "new_user_onboarding"
    override val default = false
}

object DarkModeEnabled : FeatureFlag {
    override val key = "dark_mode_enabled"
    override val default = true
}

object PremiumFeatures : FeatureFlag {
    override val key = "premium_features"
    override val default = false
}
```

### **Using Feature Flags in ViewModels**

```kotlin
class HomeViewModel(
    private val featuresRepository: FeaturesRepository
) : Component<HomeComponent.Model, HomeComponent.Event> {
    
    override fun onEvent(event: HomeComponent.Event) {
        when (event) {
            is HomeComponent.Event.CheckFeatures -> checkFeatures()
        }
    }
    
    private suspend fun checkFeatures() {
        // Get a single feature flag
        val isOnboardingEnabled = featuresRepository.get(NewUserOnboarding)
        
        // Get multiple feature flags at once
        val features = featuresRepository.get(NewUserOnboarding, DarkModeEnabled, PremiumFeatures)
        
        // Update UI based on feature flags
        updateModel { 
            it.copy(
                showOnboarding = features[NewUserOnboarding] ?: false,
                darkModeEnabled = features[DarkModeEnabled] ?: false,
                premiumFeaturesEnabled = features[PremiumFeatures] ?: false
            )
        }
    }
}
```

### **Using Feature Flags in SwiftUI (iOS)**

```swift
struct HomeView: View {
    @StateObject private var viewModel = HomeViewModel()
    
    var body: some View {
        VStack {
            if viewModel.showOnboarding {
                OnboardingView()
            }
            
            if viewModel.premiumFeaturesEnabled {
                PremiumFeaturesView()
            }
            
            // Regular content
            ContentView()
        }
        .onAppear {
            viewModel.checkFeatures()
        }
    }
}
```

### **Using Feature Flags in Compose (Android)**

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = get()
) {
    val model by viewModel.models.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.onEvent(HomeComponent.Event.CheckFeatures)
    }
    
    Column {
        if (model.showOnboarding) {
            OnboardingView()
        }
        
        if (model.premiumFeaturesEnabled) {
            PremiumFeaturesView()
        }
        
        // Regular content
        ContentView()
    }
}
```

## 🔄 Provider Switching

### **Switching Between Providers**

To switch between providers, modify the `FeatureFlagModule.kt` file:

```kotlin
// Switch to ConfigCat
single<FeatureFlagProvider> { ConfigCatProvider(get<ConfigCatClient>()) }

// Switch to Firebase Remote Config
single<FeatureFlagProvider> { RemoteConfigProvider(get<FirebaseRemoteConfig>()) }
```

### **Environment-Based Provider Selection**

Use different providers for different environments:

```kotlin
single<FeatureFlagProvider> {
    when {
        BuildKonfig.DEBUG -> {
            // Use Firebase Remote Config for development
            RemoteConfigProvider(get<FirebaseRemoteConfig>())
        }
        else -> {
            // Use ConfigCat for production
            ConfigCatProvider(get<ConfigCatClient>())
        }
    }
}
```

## 🎯 User Targeting

### **Setting User Context**

Set user properties for targeted feature flag evaluation:

```kotlin
class UserManager(
    private val featuresRepository: FeaturesRepository
) {
    suspend fun loginUser(user: User) {
        // Set user context for feature flag targeting
        featuresRepository.setUserData(
            userId = user.id,
            email = user.email,
            country = user.country
        )
    }
}
```

### **Provider-Specific Targeting**

#### **ConfigCat Targeting**

ConfigCat supports advanced targeting rules based on user attributes:

```kotlin
// User context is automatically used for targeting
val isPremiumFeatureEnabled = featuresRepository.get(PremiumFeatures)
```

#### **Firebase Remote Config Targeting**

Firebase uses Analytics user properties for targeting:

```kotlin
// User properties are set via Firebase Analytics
featuresRepository.setUserData("user123", "user@example.com", "US")
```

## 🧪 Testing

### **Unit Testing Feature Flags**

```kotlin
class FeatureFlagTest : FunSpec({
    test("should return default value when flag not found") {
        val mockProvider = mockk<FeatureFlagProvider>()
        every { mockProvider.getBoolean(any(), any()) } returns false
        
        val dataSource = FeaturesDataSource(mockProvider)
        val result = runBlocking { dataSource.get(NewUserOnboarding) }
        
        result shouldBe false
    }
    
    test("should return provider value when flag exists") {
        val mockProvider = mockk<FeatureFlagProvider>()
        every { mockProvider.getBoolean("new_user_onboarding", false) } returns true
        
        val dataSource = FeaturesDataSource(mockProvider)
        val result = runBlocking { dataSource.get(NewUserOnboarding) }
        
        result shouldBe true
    }
})
```

### **Integration Testing**

```kotlin
class FeatureFlagIntegrationTest : FunSpec({
    test("should work with real ConfigCat provider") {
        val configCatClient = ConfigCatClient("test-key")
        val provider = ConfigCatProvider(configCatClient)
        val dataSource = FeaturesDataSource(provider)
        
        val result = runBlocking { dataSource.get(NewUserOnboarding) }
        
        // Verify result based on your test configuration
        result shouldBe true
    }
})
```

## 🚀 Production Deployment

### **ConfigCat Production Setup**

1. **Create Production Environment**:
   - Set up production project in ConfigCat dashboard
   - Configure targeting rules and user segments
   - Set up A/B testing experiments

2. **Update Configuration**:
   ```kotlin
   // Update BuildKonfig with production keys
   buildConfigField(
       type = STRING,
       name = "CONFIGCAT_KEY",
       value = getSecret("CONFIGCAT_AND_LIVE_KEY"), // Production key
       nullable = false,
       const = true,
   )
   ```

### **Firebase Remote Config Production Setup**

1. **Configure Remote Config**:
   - Set up feature flags in Firebase Console
   - Configure targeting rules
   - Set up A/B testing

2. **Deploy Configuration**:
   - Publish configuration changes
   - Monitor rollout metrics
   - Set up alerts for issues

## 🐛 Troubleshooting

### **Common Issues**

#### **Feature Flags Not Updating**

**Problem**: Feature flags show old values even after updating in the provider dashboard.

**Solutions**:
1. Check cache refresh intervals
2. Force refresh: `provider.refresh()`
3. Verify network connectivity
4. Check provider configuration

#### **Provider Connection Issues**

**Problem**: Cannot connect to feature flag provider.

**Solutions**:
1. Verify API keys and configuration
2. Check network connectivity
3. Review provider-specific logs
4. Test with provider's test environment

#### **User Targeting Not Working**

**Problem**: User targeting rules are not being applied.

**Solutions**:
1. Verify user context is set correctly
2. Check targeting rule configuration
3. Review user attribute mapping
4. Test with different user segments

### **Debugging Tips**

1. **Enable Logging**:
   ```kotlin
   // ConfigCat logging
   logLevel = if (BuildKonfig.DEBUG) LogLevel.INFO else LogLevel.OFF
   ```

2. **Check Provider Status**:
   ```kotlin
   val allKeys = provider.getAllKeys()
   val allValues = provider.getAllValues()
   ```

3. **Force Refresh**:
   ```kotlin
   val refreshSuccess = provider.refresh()
   ```

## 📚 Best Practices

### **Feature Flag Design**

1. **Use Descriptive Names**:
   ```kotlin
   // Good
   object NewUserOnboarding : FeatureFlag {
       override val key = "new_user_onboarding"
       override val default = false
   }
   
   // Avoid
   object Flag1 : FeatureFlag {
       override val key = "flag1"
       override val default = false
   }
   ```

2. **Set Appropriate Defaults**:
   ```kotlin
   // Safe default for new features
   override val default = false
   
   // Safe default for existing features
   override val default = true
   ```

3. **Group Related Flags**:
   ```kotlin
   object OnboardingFlags {
       object NewUserFlow : FeatureFlag {
           override val key = "onboarding.new_user_flow"
           override val default = false
       }
       
       object SkipTutorial : FeatureFlag {
           override val key = "onboarding.skip_tutorial"
           override val default = false
       }
   }
   ```

### **Performance Optimization**

1. **Batch Flag Retrieval**:
   ```kotlin
   // Good: Get multiple flags at once
   val features = repository.get(NewUserOnboarding, DarkModeEnabled, PremiumFeatures)
   
   // Avoid: Multiple individual calls
   val onboarding = repository.get(NewUserOnboarding)
   val darkMode = repository.get(DarkModeEnabled)
   val premium = repository.get(PremiumFeatures)
   ```

2. **Cache Results**:
   ```kotlin
   class FeatureFlagCache {
       private val cache = mutableMapOf<String, Boolean>()
       
       suspend fun get(flag: FeatureFlag): Boolean {
           return cache.getOrPut(flag.key) {
               repository.get(flag)
           }
       }
   }
   ```

### **Security Considerations**

1. **Don't Expose Sensitive Data**:
   ```kotlin
   // Avoid storing sensitive information in feature flags
   object DatabasePassword : FeatureFlag {
       override val key = "db_password" // ❌ Never do this
       override val default = false
   }
   ```

2. **Validate Flag Values**:
   ```kotlin
   suspend fun getValidatedFlag(flag: FeatureFlag): Boolean {
       val value = repository.get(flag)
       // Add validation logic if needed
       return value
   }
   ```

### **Monitoring and Analytics**

1. **Track Feature Flag Usage**:
   ```kotlin
   class FeatureFlagAnalytics {
       fun trackFlagUsage(flag: FeatureFlag, value: Boolean) {
           analytics.track("feature_flag_used", mapOf(
               "flag_key" to flag.key,
               "flag_value" to value
           ))
       }
   }
   ```

2. **Monitor Performance Impact**:
   ```kotlin
   suspend fun getWithMetrics(flag: FeatureFlag): Boolean {
       val startTime = System.currentTimeMillis()
       val result = repository.get(flag)
       val duration = System.currentTimeMillis() - startTime
       
       analytics.track("feature_flag_performance", mapOf(
           "flag_key" to flag.key,
           "duration_ms" to duration
       ))
       
       return result
   }
   ```

---

**Happy feature flagging! 🚩**
