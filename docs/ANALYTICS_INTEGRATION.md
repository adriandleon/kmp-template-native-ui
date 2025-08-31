# Analytics Integration

This document covers the analytics system implementation using Firebase Analytics for tracking user events across the Kotlin Multiplatform application.

## 📋 Table of Contents

- [Overview](#overview)
- [What is Analytics?](#what-is-analytics)
- [Architecture](#architecture)
- [Setup](#setup)
- [Usage](#usage)
- [Event Tracking](#event-tracking)
- [Custom Analytics Providers](#custom-analytics-providers)
- [Testing](#testing)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

The analytics system provides a flexible way to track user events across the application with support for multiple analytics providers simultaneously. The system is designed to be easy to use, extend, and maintain across all platforms.

## 📊 What is Analytics?

Analytics helps you understand:

- **User behavior** and interaction patterns
- **Feature usage** and adoption rates
- **Performance metrics** and error tracking
- **User journey** and conversion funnels
- **App performance** and crash reporting

### **Key Benefits**

- **Cross-platform consistency** with unified API
- **Multiple provider support** for flexibility
- **Type-safe event tracking** with predefined events
- **Easy integration** with dependency injection
- **Comprehensive testing** support

## 🏗️ Architecture

### **Analytics System Structure**

```
shared/
├── src/
│   ├── commonMain/
│   │   └── kotlin/
│   │       └── com/yourcompany/yourapp/
│   │           └── analytics/
│   │               ├── domain/
│   │               │   ├── Analytics.kt           # Main analytics interface
│   │               │   ├── AnalyticsEvent.kt      # Event data class
│   │               │   └── AnalyticsProvider.kt   # Provider interface
│   │               ├── data/
│   │               │   ├── AnalyticsImpl.kt       # Main implementation
│   │               │   └── providers/
│   │               │       └── FirebaseAnalyticsProvider.kt
│   │               └── events/
│   │                   ├── CommonAnalyticsEvent.kt # Predefined events
│   │                   └── AnalyticsEvents.kt     # Event constants
│   ├── androidMain/
│   │   └── kotlin/
│   │       └── com/yourcompany/yourapp/
│   │           └── analytics/
│   │               └── providers/
│   │                   └── AndroidAnalyticsProvider.kt
│   └── iosMain/
│       └── kotlin/
│           └── com/yourcompany/yourapp/
│               └── analytics/
│                   └── providers/
│                       └── IOSAnalyticsProvider.kt
```

### **Core Interfaces**

#### Analytics Interface
```kotlin
interface Analytics {
    fun track(event: AnalyticsEvent)
    fun track(events: List<AnalyticsEvent>)
}
```

#### Analytics Provider Interface
```kotlin
interface AnalyticsProvider {
    fun track(event: AnalyticsEvent)
}
```

#### Analytics Event Data Class
```kotlin
data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any>
)
```

## ⚙️ Setup

### **Dependencies**

#### Add Firebase Analytics to Version Catalog
```toml
# In gradle/libs.versions.toml
[versions]
firebase-gitlive-sdk = "2.1.0"

[libraries]
gitlive-firebase-analytics = { module = "dev.gitlive:firebase-analytics", version.ref = "firebase-gitlive-sdk" }
```

#### Add to Shared Module
```kotlin
// In shared/build.gradle.kts
dependencies {
    commonMainImplementation(libs.gitlive.firebase.analytics)
}
```

### **Firebase Project Setup**

#### 1. Create Firebase Project
1. Navigate to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Choose a project name and follow the setup wizard
4. Enable Google Analytics (optional but recommended)

#### 2. Configure Android App
1. Click Android icon to add Android app
2. Enter package name (e.g., `com.yourcompany.yourapp`)
3. Add suffix "Android" to project nickname for clarity
4. Download `google-services.json` file
5. Place in `composeApp/google-services.json`
6. **Important**: Don't commit this file to git

#### 3. Configure iOS App
1. Click iOS icon to add iOS app
2. Enter bundle ID from `iosApp/Configuration/Config.xcconfig`
3. Add suffix "iOS" to project nickname for clarity
4. Download `GoogleService-Info.plist` file
5. Place in `iosApp/Template/GoogleService-Info.plist`
6. **Important**: Don't commit this file to git

### **Dependency Injection Setup**

#### Analytics Module
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/di/AnalyticsModule.kt
val analyticsModule = module {
    factoryOf(::analyticsProviders)
    singleOf(::AnalyticsImpl) { bind<Analytics>() }
}

private fun analyticsProviders(): List<AnalyticsProvider> = listOf(
    FirebaseAnalyticsProvider()
)
```

## 📖 Usage

### **Basic Event Tracking**

#### Inject Analytics
```kotlin
class UserRepository(
    private val analytics: Analytics
) {
    fun getUser(id: String): User? {
        analytics.track(
            CommonAnalyticsEvent.ScreenView(
                screenName = "UserProfile",
                screenClass = "UserRepository"
            )
        )
        
        return try {
            // Fetch user logic
            analytics.track(CommonAnalyticsEvent.ButtonClick("FetchUser"))
            user
        } catch (e: Exception) {
            analytics.track(
                CommonAnalyticsEvent.Error("Failed to fetch user: $id")
            )
            null
        }
    }
}
```

#### Use in Composables (Android)
```kotlin
@Composable
fun UserScreen(
    viewModel: UserViewModel = koinViewModel()
) {
    val analytics: Analytics = koinInject()
    
    LaunchedEffect(Unit) {
        analytics.track(
            CommonAnalyticsEvent.ScreenView(
                screenName = "UserScreen",
                screenClass = "UserScreen"
            )
        )
    }
    
    // UI implementation
}
```

#### Use in SwiftUI Views (iOS)
```swift
struct UserView: View {
    @StateObject private var viewModel = UserViewModel()
    private let analytics: Analytics = KoinHelper.shared.analytics
    
    var body: some View {
        VStack {
            // UI implementation
        }
        .onAppear {
            analytics.track(event: CommonAnalyticsEvent.ScreenView(
                screenName: "UserView",
                screenClass: "UserView"
            ))
        }
    }
}
```

## 🎯 Event Tracking

### **Predefined Common Events**

The system provides several predefined events for consistent tracking:

#### Screen View Events
```kotlin
// Track when a screen is viewed
analytics.track(
    CommonAnalyticsEvent.ScreenView(
        screenName = "HomeScreen",
        screenClass = "HomeComponent"
    )
)
```

#### Button Click Events
```kotlin
// Track button clicks
analytics.track(CommonAnalyticsEvent.ButtonClick("LoginButton"))
analytics.track(CommonAnalyticsEvent.ButtonClick("SignUpButton"))
```

#### Content Selection Events
```kotlin
// Track content selection
analytics.track(
    CommonAnalyticsEvent.SelectContent(
        itemId = "video123",
        contentType = "video"
    )
)

// Track item selection from lists
analytics.track(
    CommonAnalyticsEvent.SelectItem(
        itemListId = "category_list",
        itemListName = "Technology"
    )
)
```

#### Error Tracking Events
```kotlin
// Track errors with context
analytics.track(
    CommonAnalyticsEvent.Error("Network request failed")
)

// Track errors with throwable
try {
    // Risky operation
} catch (e: Exception) {
    analytics.track(
        CommonAnalyticsEvent.Error("Operation failed: ${e.message}")
    )
}
```

### **Event Constants**

All event names and parameter keys are defined as constants:

#### Event Names
```kotlin
object AnalyticsEvents {
    const val SCREEN_VIEW = "screen_view"
    const val BUTTON_CLICK = "button_click"
    const val SELECT_CONTENT = "select_content"
    const val SELECT_ITEM = "select_item"
    const val ERROR = "error"
    const val ELEMENT_TAP = "element_tap"
}
```

#### Parameter Keys
```kotlin
object AnalyticsParam {
    const val CONTENT_TYPE = "content_type"
    const val SCREEN_NAME = "screen_name"
    const val SCREEN_CLASS = "screen_class"
    const val ITEM_ID = "item_id"
    const val ITEM_LIST_ID = "item_list_id"
    const val ITEM_LIST_NAME = "item_list_name"
    const val BUTTON_NAME = "button_name"
    const val ELEMENT_NAME = "element_name"
    const val ERROR_MESSAGE = "error_message"
}
```

### **Custom Events**

#### Create Custom Event
```kotlin
// Define custom event
data class CustomAnalyticsEvent(
    val action: String,
    val category: String,
    val label: String? = null,
    val value: Int? = null
) : AnalyticsEvent {
    override val name: String = "custom_event"
    override val parameters: Map<String, Any> = buildMap {
        put("action", action)
        put("category", category)
        label?.let { put("label", it) }
        value?.let { put("value", it) }
    }
}

// Use custom event
analytics.track(
    CustomAnalyticsEvent(
        action = "purchase",
        category = "ecommerce",
        label = "premium_subscription",
        value = 999
    )
)
```

## 🔌 Custom Analytics Providers

### **Implementing a New Provider**

#### 1. Create Provider Class
```kotlin
class MyCustomProvider : AnalyticsProvider {
    override fun track(event: AnalyticsEvent) {
        // Implement tracking logic here
        val eventName = event.name
        val parameters = event.parameters
        
        // Send to your analytics service
        myAnalyticsService.logEvent(eventName, parameters)
    }
}
```

#### 2. Add to Dependency Injection
```kotlin
private fun analyticsProviders(): List<AnalyticsProvider> = listOf(
    FirebaseAnalyticsProvider(),
    MyCustomProvider()  // Add your custom provider
)
```

#### 3. Platform-Specific Providers
```kotlin
// Android-specific provider
class AndroidAnalyticsProvider : AnalyticsProvider {
    override fun track(event: AnalyticsEvent) {
        // Use Android-specific analytics (e.g., Google Analytics)
        androidAnalytics.logEvent(event.name, event.parameters)
    }
}

// iOS-specific provider
class IOSAnalyticsProvider : AnalyticsProvider {
    override fun track(event: AnalyticsEvent) {
        // Use iOS-specific analytics (e.g., Firebase Analytics)
        iosAnalytics.logEvent(event.name, event.parameters)
    }
}
```

## 🧪 Testing

### **Test Analytics Implementation**

The project includes a `TestAnalytics` implementation for testing:

#### Test Analytics Class
```kotlin
class TestAnalytics : Analytics {
    private val _events = mutableListOf<AnalyticsEvent>()
    val events: List<AnalyticsEvent> = _events
    val lastTrackEvent: AnalyticsEvent get() = events.last()

    fun reset() {
        _events.clear()
    }

    override fun track(event: AnalyticsEvent) {
        _events.add(event)
    }

    override fun track(events: List<AnalyticsEvent>) {
        _events.addAll(events)
    }
}
```

#### Using Test Analytics
```kotlin
class UserRepositoryTest {
    private val testAnalytics = TestAnalytics()
    private val repository = UserRepository(testAnalytics)
    
    @Test
    fun `should track screen view when fetching user`() {
        // When
        repository.getUser("user123")
        
        // Then
        testAnalytics.events shouldHaveSize 1
        testAnalytics.lastTrackEvent.name shouldBe "screen_view"
        testAnalytics.lastTrackEvent.parameters["screen_name"] shouldBe "UserProfile"
    }
    
    @Test
    fun `should track error when user fetch fails`() {
        // Given
        // Mock failure scenario
        
        // When
        val result = repository.getUser("nonexistent")
        
        // Then
        result shouldBe null
        testAnalytics.events.any { it.name == "error" } shouldBe true
    }
}
```

## 📚 Best Practices

### **1. Event Naming**
- **Use snake_case** for event names
- **Be descriptive but concise**
- **Use predefined constants** from `AnalyticsEvents`
- **Maintain consistency** across the app

### **2. Parameter Usage**
- **Use predefined parameter constants** from `AnalyticsParam`
- **Keep parameter names consistent** across events
- **Document custom parameters** clearly
- **Use appropriate parameter types** (String, Int, Long, etc.)

### **3. Provider Implementation**
- **Handle errors gracefully** without breaking the app
- **Add logging for debugging** when needed
- **Document provider-specific limitations**
- **Keep providers independent** and focused

### **4. Event Tracking Strategy**
- **Track important user actions** (clicks, selections, errors)
- **Include relevant context** in parameters
- **Avoid tracking sensitive information**
- **Use consistent event patterns** across similar actions

### **5. Performance Considerations**
- **Batch events** when possible for efficiency
- **Use appropriate log levels** for different environments
- **Avoid expensive operations** in tracking code
- **Consider offline event queuing** for poor network conditions

## 🐛 Troubleshooting

### **Common Issues**

#### 1. **Events Not Appearing in Firebase**
```kotlin
// Check Firebase configuration
// Verify google-services.json and GoogleService-Info.plist are correct
// Check Firebase project settings and app configuration
// Verify internet connectivity and Firebase service status
```

#### 2. **Analytics Not Initialized**
```kotlin
// Check dependency injection setup
val analytics: Analytics = koinInject()
analytics.track(CommonAnalyticsEvent.ButtonClick("TestButton"))

// Verify Koin module is included in app initialization
```

#### 3. **Custom Provider Not Working**
```kotlin
// Check provider is added to analyticsProviders list
// Verify provider implementation handles errors gracefully
// Check provider-specific configuration and credentials
```

### **Debug Mode**

#### Enable Analytics Debugging
```kotlin
// Add debug logging to custom providers
class DebugAnalyticsProvider : AnalyticsProvider {
    override fun track(event: AnalyticsEvent) {
        println("Analytics Event: ${event.name}")
        println("Parameters: ${event.parameters}")
        
        // Forward to actual provider
        actualProvider.track(event)
    }
}
```

#### Check Event Flow
```kotlin
// Verify events are being tracked
val testAnalytics = TestAnalytics()
val component = MyComponent(testAnalytics)

component.performAction()

println("Tracked events: ${testAnalytics.events}")
```

## 🔗 Related Documentation

- [Firebase Integration](FIREBASE_INTEGRATION.md) - Firebase setup and configuration
- [Unit Tests Shared](UNIT_TESTS_SHARED.md) - Testing analytics implementations
- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD analytics configuration
- [Logging Multiplatform](LOGGING_MULTIPLATFORM.md) - Cross-platform logging

## 📖 Resources

- [Firebase Analytics Documentation](https://firebase.google.com/docs/analytics)
- [Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)
- [Analytics Best Practices](https://support.google.com/analytics/answer/1009614)
- [Mobile Analytics Guide](https://firebase.google.com/docs/analytics/guides)

---

**Track user behavior and app performance with comprehensive analytics! 🚀** 