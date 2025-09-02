# Multiplatform Logging

This document covers the multiplatform logging setup using Kermit for consistent logging across Android, iOS, and shared modules.

## 📋 Table of Contents

- [🎯 Overview](#-overview)
- [🚀 What is Kermit?](#-what-is-kermit)
- [🏗️ Architecture](#️-architecture)
- [⚙️ Setup](#️-setup)
- [📖 Usage](#-usage)
- [🔧 Configuration](#-configuration)
- [📱 Platform-Specific Logging](#-platform-specific-logging)
- [📚 Best Practices](#-best-practices)
- [🐛 Troubleshooting](#-troubleshooting)

## 🎯 Overview

The project uses **Kermit** as the primary logging solution for Kotlin Multiplatform, providing:

- **Consistent logging API** across all platforms
- **Platform-specific implementations** for optimal performance
- **Configurable log levels** and output formats
- **Integration with dependency injection** via Koin
- **Structured logging** for better debugging and monitoring

## 🚀 What is Kermit?

[Kermit](https://github.com/touchlab/Kermit) is a Kotlin Multiplatform logging library that:

- **Works on all KMP targets** (Android, iOS, JVM, JS, Native)
- **Provides a unified API** for logging across platforms
- **Supports multiple log levels** (Verbose, Debug, Info, Warn, Error, Assert)
- **Integrates with platform logging** (Android Logcat, iOS Console)
- **Supports custom loggers** and output formats
- **Performance optimized** for mobile platforms

## 🏗️ Architecture

### **Logging Structure**

```
shared/
├── src/
│   ├── commonMain/
│   │   └── kotlin/
│   │       └── com/yourcompany/yourapp/
│   │           └── logger/
│   │               ├── domain/
│   │               │   └── Logger.kt          # Logger interface
│   │               └── data/
│   │                   └── KermitLoggerImpl.kt # Kermit implementation
│   ├── androidMain/
│   │   └── kotlin/
│   │       └── com/yourcompany/yourapp/
│   │           └── logger/
│   │               └── AndroidLoggerImpl.kt   # Android-specific logging
│   └── iosMain/
│       └── kotlin/
│           └── com/yourcompany/yourapp/
│               └── logger/
│                   └── IOSLoggerImpl.kt       # iOS-specific logging
```

### **Logger Interface**

```kotlin
interface Logger {
    fun error(throwable: Throwable, message: String? = null)
    fun error(message: String)
    fun warn(message: String)
    fun info(message: String)
    fun debug(message: String)
    fun verbose(message: String)
}
```

## ⚙️ Setup

### **Dependencies**

#### Add Kermit to Version Catalog
```toml
# In gradle/libs.versions.toml
[versions]
kermit = "2.0.8"

[libraries]
kermit = { module = "co.touchlab:kermit", version.ref = "kermit" }
```

#### Add to Shared Module
```kotlin
// In shared/build.gradle.kts
dependencies {
    commonMainImplementation(libs.kermit)
}
```

### **Logger Implementation**

#### Kermit Logger Implementation
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/logger/data/KermitLoggerImpl.kt
class KermitLoggerImpl : Logger {
    private val logger = Logger("AppLogger")
    
    override fun error(throwable: Throwable, message: String?) {
        logger.e(throwable, message ?: throwable.message ?: "Unknown error")
    }
    
    override fun error(message: String) {
        logger.e(message)
    }
    
    override fun warn(message: String) {
        logger.w(message)
    }
    
    override fun info(message: String) {
        logger.i(message)
    }
    
    override fun debug(message: String) {
        logger.d(message)
    }
    
    override fun verbose(message: String) {
        logger.v(message)
    }
}
```

#### Platform-Specific Implementations

**Android Implementation:**
```kotlin
// In shared/src/androidMain/kotlin/com/yourcompany/yourapp/logger/AndroidLoggerImpl.kt
class AndroidLoggerImpl : Logger {
    private val logger = Logger("AppLogger")
    
    override fun error(throwable: Throwable, message: String?) {
        logger.e(throwable, message ?: throwable.message ?: "Unknown error")
    }
    
    // ... other methods
}
```

**iOS Implementation:**
```kotlin
// In shared/src/iosMain/kotlin/com/yourcompany/yourapp/logger/IOSLoggerImpl.kt
class IOSLoggerImpl : Logger {
    private val logger = Logger("AppLogger")
    
    override fun error(throwable: Throwable, message: String?) {
        logger.e(throwable, message ?: throwable.message ?: "Unknown error")
    }
    
    // ... other methods
}
```

### **Dependency Injection**

#### Koin Module Configuration
```kotlin
// In shared/src/commonMain/kotlin/com/yourcompany/yourapp/di/LoggerModule.kt
val loggerModule = module {
    single<Logger> { KermitLoggerImpl() }
}
```

#### Platform-Specific Modules
```kotlin
// In shared/src/androidMain/kotlin/com/yourcompany/yourapp/di/AndroidLoggerModule.kt
val androidLoggerModule = module {
    single<Logger> { AndroidLoggerImpl() }
}

// In shared/src/iosMain/kotlin/com/yourcompany/yourapp/di/IOSLoggerModule.kt
val iosLoggerModule = module {
    single<Logger> { IOSLoggerImpl() }
}
```

## 📖 Usage

### **Basic Logging**

#### Inject Logger
```kotlin
class UserRepository(
    private val logger: Logger
) {
    fun getUser(id: String): User? {
        logger.debug("Fetching user with ID: $id")
        
        return try {
            // Fetch user logic
            logger.info("Successfully fetched user: $id")
            user
        } catch (e: Exception) {
            logger.error(e, "Failed to fetch user: $id")
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
    val logger: Logger = koinInject()
    
    LaunchedEffect(Unit) {
        logger.info("UserScreen displayed")
    }
    
    // UI implementation
}
```

#### Use in SwiftUI Views (iOS)
```swift
struct UserView: View {
    @StateObject private var viewModel = UserViewModel()
    private let logger: Logger = KoinHelper.shared.logger
    
    var body: some View {
        VStack {
            // UI implementation
        }
        .onAppear {
            logger.info(message: "UserView displayed")
        }
    }
}
```

### **Log Levels**

#### Error Logging
```kotlin
// Log exceptions with context
try {
    // Risky operation
} catch (e: Exception) {
    logger.error(e, "Operation failed")
}

// Log error messages
logger.error("Network request failed")
```

#### Warning Logging
```kotlin
// Log warnings for potential issues
if (user.age < 18) {
    logger.warn("User is underage: ${user.age}")
}
```

#### Info Logging
```kotlin
// Log important application events
logger.info("User logged in: ${user.email}")
logger.info("App started successfully")
```

#### Debug Logging
```kotlin
// Log debug information
logger.debug("Processing user data: $userData")
logger.debug("API response received: $response")
```

#### Verbose Logging
```kotlin
// Log detailed information for debugging
logger.verbose("Function parameters: $param1, $param2")
logger.verbose("Internal state: $state")
```

## 🔧 Configuration

### **Log Level Configuration**

#### Set Global Log Level
```kotlin
// In your app initialization
Logger.setMinSeverity(Severity.Debug)
```

#### Configure Platform-Specific Logging
```kotlin
// Android-specific configuration
Logger.setMinSeverity(Severity.Info) // Only show Info and above in production

// iOS-specific configuration
Logger.setMinSeverity(Severity.Debug) // Show more logs in iOS for debugging
```

### **Custom Logger Configuration**

#### Create Custom Logger
```kotlin
class CustomLogger(
    private val tag: String,
    private val minLevel: Severity = Severity.Debug
) : Logger {
    private val logger = Logger(tag, minLevel)
    
    override fun error(throwable: Throwable, message: String?) {
        logger.e(throwable, message ?: throwable.message ?: "Unknown error")
    }
    
    // ... other methods
}
```

#### Configure Logger with Filters
```kotlin
val logger = Logger("AppLogger").apply {
    setMinSeverity(Severity.Info)
    addLogWriter(PlatformLogWriter())
}
```

### **Output Configuration**

#### Configure Log Writers
```kotlin
// Add custom log writer
Logger.addLogWriter(CustomLogWriter())

// Remove default log writer
Logger.removeLogWriter(PlatformLogWriter())
```

#### Custom Log Writer
```kotlin
class CustomLogWriter : LogWriter {
    override fun log(severity: Severity, message: String, tag: String?, throwable: Throwable?) {
        // Custom logging logic
        println("[$severity] $tag: $message")
        throwable?.printStackTrace()
    }
}
```

## 📱 Platform-Specific Logging

### **Android Logging**

#### Android Logcat Integration
```kotlin
class AndroidLoggerImpl : Logger {
    private val logger = Logger("AppLogger").apply {
        addLogWriter(PlatformLogWriter())
    }
    
    // ... implementation
}
```

#### Android-Specific Configuration
```kotlin
// In Android app initialization
if (BuildConfig.DEBUG) {
    Logger.setMinSeverity(Severity.Debug)
} else {
    Logger.setMinSeverity(Severity.Warn)
}
```

### **iOS Logging**

#### iOS Console Integration
```kotlin
class IOSLoggerImpl : Logger {
    private val logger = Logger("AppLogger").apply {
        addLogWriter(PlatformLogWriter())
    }
    
    // ... implementation
}
```

#### iOS-Specific Configuration
```kotlin
// In iOS app initialization
#if DEBUG
Logger.setMinSeverity(Severity.Debug)
#else
Logger.setMinSeverity(Severity.Warn)
#endif
```

## 📚 Best Practices

### **1. Log Level Usage**
- **Error**: Use for exceptions and critical failures
- **Warn**: Use for potential issues and deprecations
- **Info**: Use for important application events
- **Debug**: Use for detailed debugging information
- **Verbose**: Use for very detailed debugging information

### **2. Log Message Content**
- **Include context** in log messages
- **Use structured logging** for complex data
- **Avoid sensitive information** in logs
- **Make messages actionable** and clear

### **3. Performance Considerations**
- **Use appropriate log levels** for production
- **Avoid expensive operations** in log messages
- **Use lazy evaluation** for complex log messages
- **Configure log levels** based on build variants

### **4. Security and Privacy**
- **Never log sensitive data** (passwords, tokens, PII)
- **Sanitize log messages** before output
- **Use log level filtering** in production
- **Implement log rotation** for file-based logging

### **5. Integration with Monitoring**
- **Send error logs** to crash reporting services
- **Use structured logging** for analytics
- **Implement log aggregation** for distributed systems
- **Monitor log patterns** for system health

## 🐛 Troubleshooting

### **Common Issues**

#### 1. **Logs Not Appearing**
```kotlin
// Check log level configuration
Logger.setMinSeverity(Severity.Debug)

// Verify logger is properly injected
val logger: Logger = koinInject()
logger.info("Test log message")
```

#### 2. **Platform-Specific Issues**
```kotlin
// Android: Check Logcat filters
// iOS: Check Console app

// Verify platform-specific implementation
Logger.addLogWriter(PlatformLogWriter())
```

#### 3. **Performance Issues**
```kotlin
// Reduce log level in production
Logger.setMinSeverity(Severity.Warn)

// Use lazy evaluation for expensive operations
logger.debug { "Expensive operation: ${expensiveCalculation()}" }
```

### **Debug Mode**

#### Enable Verbose Logging
```kotlin
// Set to most verbose level
Logger.setMinSeverity(Severity.Verbose)

// Add custom log writer for debugging
Logger.addLogWriter(CustomDebugWriter())
```

#### Check Logger Configuration
```kotlin
// Verify logger setup
val logger = Logger("TestLogger")
logger.info("Test message")

// Check available log writers
// This depends on your specific implementation
```

## 🔗 Related Documentation

- [Unit Tests Shared](UNIT_TESTS_SHARED.md) - Testing logging implementations
- [GitHub Actions Workflows](GITHUB_ACTIONS.md) - CI/CD logging configuration
- [Kotlin Format & Lint](KOTLIN_FORMAT_LINT.md) - Code quality tools
- [Pre-Commit Hooks](PRE_COMMIT_HOOKS.md) - Local automation

## 📖 Resources

- [Kermit Documentation](https://github.com/touchlab/Kermit)
- [Kotlin Multiplatform Logging](https://kotlinlang.org/docs/multiplatform-logging.html)
- [Android Logging Best Practices](https://developer.android.com/studio/debug/am-logcat)
- [iOS Console Logging](https://developer.apple.com/documentation/os/logging)

---

**Implement consistent logging across all platforms! 🚀**
