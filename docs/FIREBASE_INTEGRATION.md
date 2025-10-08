# Firebase Integration Guide

This document covers the complete Firebase integration setup for the Kotlin Multiplatform project, including analytics, crash reporting, testing, and configuration for both Android and iOS platforms.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [🔥 What is Firebase?](#-what-is-firebase)
- [🏗️ Architecture](#-architecture)
- [⚙️ Setup](#-setup)
- [🔧 Configuration](#-configuration)
- [📊 Analytics](#-analytics)
- [💥 Crash Reporting](#-crash-reporting)
- [🧪 Firebase Test Lab](#-firebase-test-lab)
- [✅ Testing](#-testing)
- [🐛 Troubleshooting](#-troubleshooting)
- [📚 Best Practices](#-best-practices)

## 🎯 Overview

Firebase provides a comprehensive suite of tools for mobile app development, including analytics, crash reporting, testing, and cloud services. This integration enables cross-platform analytics tracking, automated testing, and crash monitoring for both Android and iOS apps.

## 🔥 What is Firebase?

Firebase is Google's mobile and web application development platform that provides tools and infrastructure to help developers build high-quality apps. Key services include:

- **Analytics**: User behavior and app performance insights
- **Crashlytics**: Real-time crash reporting and analysis
- **Test Lab**: Automated testing on real devices
- **Cloud Messaging**: Push notifications and messaging
- **Remote Config**: Dynamic app configuration
- **Performance Monitoring**: App performance insights

## 🏗️ Architecture

### **Multiplatform Integration**

The Firebase integration follows a layered architecture pattern:

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
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              Shared Module (Kotlin)                     │ │
│  │  ┌─────────────────┐  ┌─────────────────┐              │ │
│  │  │   Analytics     │  │  Crash Reporting │              │ │
│  │  │   Interface     │  │   Interface     │              │ │
│  │  └─────────────────┘  └─────────────────┘              │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Platform Layer                          │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │   Android       │  │      iOS        │                  │
│  │  Firebase SDK   │  │   Firebase SDK  │                  │ │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### **Key Components**

1. **Shared Module**: Platform-agnostic interfaces and business logic
2. **Platform Implementations**: Android and iOS specific Firebase SDK usage
3. **Dependency Injection**: Koin-based service management
4. **Analytics Abstraction**: Unified event tracking across platforms
5. **Crash Reporting**: Centralized error monitoring

## 🚀 Setup

### **1. Create Firebase Project**

1. **Navigate to [Firebase Console](https://console.firebase.google.com/)**
2. **Click "Create a project"**
3. **Enter project name** (e.g., "YourApp-Firebase")
4. **Enable Google Analytics** (recommended)
5. **Choose Analytics account** or create new
6. **Click "Create project"**

### **2. Add Android App**

1. **Click "Add app" > Android**
2. **Enter package name**: `com.yourcompany.yourapp`
3. **Enter app nickname** (optional)
4. **Click "Register app"**
5. **Download `google-services.json`**
6. **Place in `composeApp/` directory**

### **3. Add iOS App**

1. **Click "Add app" > iOS**
2. **Enter bundle ID**: `com.yourcompany.yourapp`
3. **Enter app nickname** (optional)
4. **Click "Register app"**
5. **Download `GoogleService-Info.plist`**
6. **Place in `iosApp/KMP-Template/` directory**

### **4. Enable Services**

#### **Analytics**
1. **Go to Analytics > Dashboard**
2. **Verify data collection** is active
3. **Set up custom events** if needed

#### **Crashlytics**
1. **Go to Crashlytics**
2. **Click "Enable Crashlytics"**
3. **Follow setup instructions** for each platform

#### **Test Lab**
1. **Go to Test Lab**
2. **Enable API** if prompted
3. **Configure test devices**

## ⚙️ Configuration

### **1. Dependencies**

#### **Shared Module**
```kotlin
// In shared/build.gradle.kts
kotlin {
    sourceSets {
        commonMain.dependencies {
            // Firebase Analytics
            implementation("com.google.firebase:firebase-analytics-ktx:21.5.0")
            
            // Crashlytics
            implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.2")
        }
        
        androidMain.dependencies {
            // Android-specific Firebase dependencies
            implementation("com.google.firebase:firebase-analytics-ktx:21.5.0")
            implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.2")
        }
        
        iosMain.dependencies {
            // iOS-specific Firebase dependencies
            implementation("com.google.firebase:firebase-analytics-ktx:21.5.0")
            implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.2")
        }
    }
}
```

#### **Android Module**
```kotlin
// In composeApp/build.gradle.kts
plugins {
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
}
```

#### **iOS Module**
```swift
// In iosApp/KMP-Template.xcodeproj
// Add Firebase pods to Podfile
pod 'Firebase/Analytics'
pod 'Firebase/Crashlytics'
```

### **2. Configuration Files**


#### **Android Configuration**

The android configuration file should be placed in `composeApp/google-services.json`

#### **iOS Configuration**

The iOS configuration file should be placed in `iosApp/KMP-Template/GoogleService-Info.plist`

### **3. GitHub Secrets**

Configure these secrets in your GitHub repository:

```yaml
# Required Firebase secrets
GOOGLE_SERVICES_JSON: "base64_encoded_google_services_json"
GOOGLE_PROJECT_ID: "your-firebase-project-id"
GOOGLE_SERVICE_ACCOUNT: "base64_encoded_service_account_json"
```

#### **Convert Configuration Files to Base64**
```bash
# Android configuration
base64 -i composeApp/google-services.json -o google_services_base64.txt

# iOS configuration
base64 -i iosApp/KMP-Template/GoogleService-Info.plist -o google_service_info_base64.txt

# Service account key
base64 -i service-account-key.json -o service_account_base64.txt
```

## 📊 Analytics

### **1. Analytics Interface**

#### **Shared Analytics Interface**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/analytics/Analytics.kt
interface Analytics {
    fun trackEvent(event: AnalyticsEvent)
    fun setUserProperty(key: String, value: String)
    fun setUserId(userId: String)
}

data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any> = emptyMap()
)
```

#### **Firebase Analytics Implementation**
```kotlin
// In shared/src/androidMain/kotlin/com/yourcompany/yourapp/analytics/FirebaseAnalyticsProvider.kt
internal class FirebaseAnalyticsProvider : AnalyticsProvider {
    private val firebaseAnalytics: FirebaseAnalytics = Firebase.analytics
    
    override fun track(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name, event.parameters)
    }
    
    override fun setUserProperty(key: String, value: String) {
        firebaseAnalytics.setUserProperty(key, value)
    }
    
    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }
}
```

### **2. Event Tracking**

#### **Predefined Events**
```kotlin
// Common app events
val events = object {
    val appOpen = AnalyticsEvent("app_open")
    val userLogin = AnalyticsEvent("user_login", mapOf("method" to "email"))
    val purchase = AnalyticsEvent("purchase", mapOf("value" to 9.99, "currency" to "USD"))
    val screenView = AnalyticsEvent("screen_view", mapOf("screen_name" to "home"))
}
```

#### **Custom Event Tracking**
```kotlin
// Track custom events
analytics.trackEvent(
    AnalyticsEvent(
        name = "feature_used",
        parameters = mapOf(
            "feature_name" to "search",
            "search_term" to "kotlin multiplatform",
            "results_count" to 42
        )
    )
)
```

### **3. User Properties**

#### **Set User Properties**
```kotlin
// Set user properties for segmentation
analytics.setUserProperty("user_type", "premium")
analytics.setUserProperty("subscription_plan", "monthly")
analytics.setUserProperty("app_version", "1.2.3")
```

#### **Set User ID**
```kotlin
// Set user ID for cross-platform tracking
analytics.setUserId("user_12345")
```

## 🚨 Crash Reporting

### **1. Crashlytics Interface**

#### **Shared Crash Reporting Interface**
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/crashlytics/Crashlytics.kt
interface Crashlytics {
    fun recordException(throwable: Throwable)
    fun log(message: String)
    fun setCustomKey(key: String, value: String)
    fun setUserId(userId: String)
}
```

#### **Firebase Crashlytics Implementation**
```kotlin
// In shared/src/androidMain/kotlin/com/yourcompany/yourapp/crashlytics/FirebaseCrashlyticsProvider.kt
internal class FirebaseCrashlyticsProvider : CrashlyticsProvider {
    private val crashlytics: FirebaseCrashlytics = Firebase.crashlytics
    
    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
    
    override fun log(message: String) {
        crashlytics.log(message)
    }
    
    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
    
    override fun setUserId(userId: String) {
        crashlytics.setUserId(userId)
    }
}
```

### **2. Error Handling**

#### **Record Exceptions**
```kotlin
// Record exceptions automatically
try {
    // Risky operation
    riskyOperation()
} catch (e: Exception) {
    crashlytics.recordException(e)
    // Handle error gracefully
}
```

#### **Custom Logging**
```kotlin
// Log important events
crashlytics.log("User completed onboarding")
crashlytics.log("Payment processing started")

// Set custom keys for debugging
crashlytics.setCustomKey("last_action", "payment_initiated")
crashlytics.setCustomKey("user_level", "premium")
```

## 🧪 Firebase Test Lab

### **1. Test Lab Configuration**

#### **Workflow Configuration**
```yaml
# In .github/workflows/android_deploy.yml
env:
  # Firebase Test Lab Configuration
  FIREBASE_DEVICE_MODEL: "shiba"      # Pixel 4
  FIREBASE_DEVICE_VERSION: "34"       # Android 14
  FIREBASE_LOCALE: "en"               # English
  FIREBASE_ORIENTATION: "portrait"    # Portrait orientation
  FIREBASE_TIMEOUT: "30m"             # Test timeout
```

#### **Test Device Selection**
```yaml
# Available device models
FIREBASE_DEVICE_MODEL: "shiba"        # Pixel 4
FIREBASE_DEVICE_MODEL: "redfin"       # Pixel 5
FIREBASE_DEVICE_MODEL: "barbet"       # Pixel 5a
FIREBASE_DEVICE_MODEL: "oriole"       # Pixel 6
FIREBASE_DEVICE_MODEL: "raven"        # Pixel 6 Pro
```

### **2. Test Execution**

#### **Run Tests on Test Lab**
```bash
# Run instrumentation tests
gcloud firebase test android run \
  --type instrumentation \
  --app app-debug.apk \
  --test app-debug-test.apk \
  --device model=shiba,version=34,locale=en,orientation=portrait \
  --timeout 30m
```

#### **View Test Results**
1. **Go to Firebase Console > Test Lab**
2. **Click on test run** to view details
3. **Download test artifacts** (screenshots, videos, logs)
4. **Analyze test failures** and performance metrics

## 🧪 Testing

### **1. Unit Testing**

#### **Test Analytics Implementation**
```kotlin
// In shared/src/commonTest/kotlin/com/yourcompany/yourapp/analytics/AnalyticsTest.kt
class AnalyticsTest {
    private val mockAnalytics = mockk<Analytics>(relaxed = true)
    
    @Test
    fun `track event calls analytics provider`() {
        // Given
        val event = AnalyticsEvent("test_event", mapOf("key" to "value"))
        
        // When
        mockAnalytics.trackEvent(event)
        
        // Then
        verify { mockAnalytics.trackEvent(event) }
    }
}
```

#### **Test Crashlytics Implementation**
```kotlin
// In shared/src/commonTest/kotlin/com/yourcompany/yourapp/crashlytics/CrashlyticsTest.kt
class CrashlyticsTest {
    private val mockCrashlytics = mockk<Crashlytics>(relaxed = true)
    
    @Test
    fun `record exception calls crashlytics provider`() {
        // Given
        val exception = RuntimeException("Test exception")
        
        // When
        mockCrashlytics.recordException(exception)
        
        // Then
        verify { mockCrashlytics.recordException(exception) }
    }
}
```

### **2. Integration Testing**

#### **Test Firebase Integration**
```kotlin
// In composeApp/src/androidTest/kotlin/com/yourcompany/yourapp/FirebaseIntegrationTest.kt
@RunWith(AndroidJUnit4::class)
class FirebaseIntegrationTest {
    @Test
    fun testFirebaseAnalyticsInitialization() {
        // Verify Firebase is properly initialized
        assertNotNull(Firebase.analytics)
    }
    
    @Test
    fun testFirebaseCrashlyticsInitialization() {
        // Verify Crashlytics is properly initialized
        assertNotNull(Firebase.crashlytics)
    }
}
```

### **3. Test Data**

#### **Mock Analytics Provider**
```kotlin
// In shared/src/commonTest/kotlin/com/yourcompany/yourapp/analytics/TestAnalytics.kt
class TestAnalytics : Analytics {
    private val events = mutableListOf<AnalyticsEvent>()
    private val userProperties = mutableMapOf<String, String>()
    private var userId: String? = null
    
    override fun trackEvent(event: AnalyticsEvent) {
        events.add(event)
    }
    
    override fun setUserProperty(key: String, value: String) {
        userProperties[key] = value
    }
    
    override fun setUserId(userId: String) {
        this.userId = userId
    }
    
    fun getEvents(): List<AnalyticsEvent> = events.toList()
    fun getUserProperties(): Map<String, String> = userProperties.toMap()
    fun getUserId(): String? = userId
}
```

## 🐛 Troubleshooting

### **Common Issues**

#### **1. Configuration File Issues**

**Problem**: Firebase not initializing properly
**Solution**:
```bash
# Verify configuration files exist
ls -la composeApp/google-services.json
ls -la iosApp/KMP-Template/GoogleService-Info.plist

# Check file permissions
chmod 644 composeApp/google-services.json
chmod 644 iosApp/KMP-Template/GoogleService-Info.plist
```

#### **2. Dependency Issues**

**Problem**: Build fails with Firebase dependencies
**Solution**:
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Check dependency tree
./gradlew :composeApp:dependencies --configuration implementation
```

#### **3. Analytics Not Working**

**Problem**: Events not appearing in Firebase Console
**Solution**:
1. **Verify internet connection**
2. **Check Firebase project configuration**
3. **Wait 24-48 hours** for data to appear
4. **Enable debug logging** in development

#### **4. Test Lab Failures**

**Problem**: Tests fail on Firebase Test Lab
**Solution**:
1. **Check device compatibility**
2. **Verify test timeout settings**
3. **Review test logs** for specific errors
4. **Test locally first** before uploading

### **Debug Mode**

#### **Enable Firebase Debug Logging**
```kotlin
// In Android MainActivity or iOS AppDelegate
if (BuildConfig.DEBUG) {
    FirebaseApp.initializeApp(this)
    FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true)
}
```

#### **Verify Firebase Initialization**
```kotlin
// Check if Firebase is properly initialized
try {
    val analytics = Firebase.analytics
    Log.d("Firebase", "Analytics initialized successfully")
} catch (e: Exception) {
    Log.e("Firebase", "Failed to initialize analytics", e)
}
```

## 📚 Best Practices

### **1. Analytics Strategy**
- **Define clear event names** with consistent naming conventions
- **Use meaningful parameters** for better insights
- **Avoid tracking sensitive information** (PII)
- **Set up conversion tracking** for business goals

### **2. Crash Reporting**
- **Record exceptions immediately** when they occur
- **Add context information** with custom keys
- **Group related crashes** with consistent naming
- **Monitor crash rates** and prioritize fixes

### **3. Testing Strategy**
- **Test Firebase integration** in CI/CD pipeline
- **Use Test Lab** for device compatibility testing
- **Mock Firebase services** in unit tests
- **Verify configuration** in different environments

### **4. Performance Optimization**
- **Batch analytics events** when possible
- **Use appropriate test device** configurations
- **Monitor API usage** and quotas
- **Optimize test execution** time

### **5. Security**
- **Never commit** configuration files to version control
- **Use GitHub secrets** for sensitive information
- **Rotate service account keys** regularly
- **Monitor access logs** for suspicious activity

## 🔗 Related Documentation

- [Analytics Integration](ANALYTICS_INTEGRATION.md) - Detailed analytics setup
- [GitHub Actions](GITHUB_ACTIONS.md) - CI/CD automation
- [Deploy Android](DEPLOY_ANDROID.md) - Android deployment
- [Deploy iOS](DEPLOY_IOS.md) - iOS deployment

## 📖 Resources

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Firebase Test Lab](https://firebase.google.com/docs/test-lab)
- [Firebase Analytics](https://firebase.google.com/docs/analytics)
- [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics)

## 📝 Additional Commands

### **Firebase CLI Commands**
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# List projects
firebase projects:list

# Use specific project
firebase use your-project-id

# Deploy to Firebase
firebase deploy
```

### **Test Lab Commands**
```bash
# List available devices
gcloud firebase test android models list

# List available OS versions
gcloud firebase test android versions list

# Run test on specific device
gcloud firebase test android run \
  --type instrumentation \
  --app app-debug.apk \
  --test app-debug-test.apk \
  --device model=shiba,version=34
```

## 📋 Notes

- **Firebase configuration files** should never be committed to version control
- **Test Lab has usage quotas** - monitor your usage to avoid exceeding limits
- **Analytics data may take 24-48 hours** to appear in the console
- **Crashlytics requires internet connection** to upload crash reports
- **Use appropriate test devices** based on your target audience

---

**Integrate Firebase seamlessly across platforms! 🔥**
