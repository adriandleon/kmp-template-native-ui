# Kotlin Multiplatform Template

![Static Badge](https://img.shields.io/badge/Compose-327CF3?style=flat&logo=android&logoColor=white)
![Static Badge](https://img.shields.io/badge/SwiftUI-FA7343?style=flat&logo=swift&logoColor=white)
![Static Badge](https://img.shields.io/badge/Kotlin-663399?&style=flat&logo=kotlin&logoColor=white)
[![Android App Deploy](https://github.com/adriandleon/kmp-template-native-ui/actions/workflows/android_deploy.yml/badge.svg?branch=main)](https://github.com/adriandleon/kmp-template-native-ui/actions/workflows/android_deploy.yml)

A modern, production-ready template for building Kotlin Multiplatform (KMP) applications with native UI targeting Android and iOS. This template provides a solid foundation for creating cross-platform apps using **Compose Multiplatform** and **SwiftUI** for the UI layer and **Kotlin Multiplatform** for the shared logic.

## 🚀 Features

- **Kotlin Multiplatform** with Kotlin 2.2.10
- **Compose Multiplatform** for Android UI
- **SwiftUI** for iOS UI
- **Decompose** for navigation and component lifecycle management
- **MVVM + MVI** architecture with MVIKotlin
- **Dependency Injection** with Koin
- **Networking** with Ktor
- **Testing** with Kotest
- **Logging** with Kermit
- **Configuration Management** with ConfigCat
- **Database** with Supabase
- **Modern Android** (API 24+, Compile SDK 36)

## 📱 Supported Platforms

- **Android**: API 24+ (Android 7.0+)
- **iOS**: iOS 18.2+
- **Shared**: Common Kotlin code

## 🛠️ Getting Started

### Prerequisites

- **Android Studio Hedgehog** (2023.1.1) or later
- **Xcode 16.0** or later (for iOS development)
- **Kotlin 2.2.10** or later
- **Java 17** or later
- **Gradle 8.0** or later

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd Template
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio
   - Sync Gradle files
   - Wait for the initial build to complete

3. **Run on Android**
   - Select an Android device or emulator
   - Click the "Run" button or press `Shift + F10`

4. **Run on iOS**
   - Open `iosApp/Template.xcodeproj` in Xcode
   - Select an iOS simulator or device
   - Press `Cmd + R` to build and run

5. **Set up GitHub Actions** (Optional)
   - Configure required secrets in your repository settings
   - Customize workflow variables for your project
   - See [GitHub Actions Workflows](docs/GITHUB_ACTIONS.md) for detailed setup

6. **Configure MCP Servers** (Optional)
   - Set up environment variables for API keys
   - Configure GitHub and Context7 MCP servers
   - See [MCP Servers Configuration](docs/MCP_SERVERS.md) for setup instructions

### Customization

1. **Update package names**
   - Replace `com.adriandeleon.template` with your package name
   - Update in `composeApp/build.gradle.kts`
   - Update in `shared/build.gradle.kts`
   - Update in `iosApp/Template/Info.plist`

2. **Update app name**
   - Android: Update `app_name` in `composeApp/src/androidMain/res/values/strings.xml`
   - iOS: Update `CFBundleDisplayName` in `iosApp/Template/Info.plist`

3. **Update bundle identifier**
   - iOS: Update `CFBundleIdentifier` in `iosApp/Template/Info.plist`

## 📚 Project Documentation

- [GitHub Actions Workflows](docs/GITHUB_ACTIONS.md) - Configurable CI/CD workflows
- [MCP Servers Configuration](docs/MCP_SERVERS.md) - AI assistance setup and configuration
- [Pre-Commit Hooks](docs/PRE_COMMIT_HOOKS.md) - Local code quality automation
- [Kotlin Format & Lint](docs/KOTLIN_FORMAT_LINT.md) - Kotlin code quality tools
- [Swift Format & Lint](docs/SWIFT_FORMAT_LINT.md) - Swift code quality tools
- [Pull Request Checks](docs/PR_DANGER_CHECKS.md) - Automated PR validation
- [Unit Tests Shared](docs/UNIT_TESTS_SHARED.md) - Testing strategies and tools
- [Code Coverage Reports](docs/CODE_COVERAGE_REPORTS.md) - Coverage configuration and reporting
- [Supabase Integration](docs/SUPABASE_INTEGRATION.md) - Backend-as-a-Service setup
- [Firebase Integration](docs/FIREBASE_INTEGRATION.md) - Analytics and crash reporting
- [Analytics Integration](docs/ANALYTICS_INTEGRATION.md) - User analytics and tracking
- [Deploy Android Version](docs/DEPLOY_ANDROID.md) - Play Store deployment guide
- [Deploy iOS Version](docs/DEPLOY_IOS.md) - App Store deployment guide
- [Logging Multiplatform](docs/LOGGING_MULTIPLATFORM.md) - Cross-platform logging setup

## 🛠️ Tech Stack & Libraries

### Architecture & Navigation
- **[Decompose](https://github.com/arkivanov/Decompose)** (3.3.0) - Navigation and component lifecycle
- **[MVIKotlin](https://github.com/arkivanov/MVIKotlin)** (4.3.0) - MVI architecture implementation
- **[Essenty](https://github.com/arkivanov/Essenty)** (2.5.0) - Lifecycle management

### Dependency Injection
- **[Koin](https://insert-koin.io/)** (4.1.0) - Dependency injection framework

### UI Framework
- **[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)** (1.8.2) - Declarative UI toolkit
- **Compose Compiler** - Kotlin compiler plugin for Compose

### Networking
- **[Ktor](https://ktor.io/)** (3.2.3) - HTTP client for networking
  - `ktor-client-okhttp` for Android
  - `ktor-client-darwin` for iOS

### Data & Storage
- **[Supabase](https://supabase.com/)** (3.2.2) - Backend-as-a-Service
- **[DataStore](https://developer.android.com/jetpack/compose/datastore)** (1.1.7) - Local data storage
- **[Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)** (1.9.0) - JSON serialization
- **[Kotlinx DateTime](https://github.com/Kotlin/kotlinx-datetime)** (0.7.1) - Date and time utilities

### Testing
- **[Kotest](https://kotest.io/)** (6.0.1) - Testing framework
- **[Mokkery](https://github.com/mockk/mokkery)** (2.9.0) - Mocking library

### Code Quality & Formatting
- **[Ktfmt](https://github.com/facebook/ktfmt)** - Kotlin code formatter following official style guide
- **[Detekt](https://detekt.dev/)** - Static code analysis tool
- **[Detekt Compose Rules](https://github.com/mrmans0n/compose-rules)** - Compose-specific linting rules

### CI/CD & Automation
- **[GitHub Actions](https://github.com/features/actions)** - Configurable CI/CD workflows for testing, building, and deployment
- **[Danger](https://github.com/danger/danger)** - Automated PR review and quality checks
- **[Codecov](https://about.codecov.io/)** - Code coverage reporting and analysis

### AI Development Assistance
- **[MCP Servers](https://modelcontextprotocol.io/)** - Model Context Protocol servers for enhanced AI assistance
- **[GitHub MCP Server](https://github.com/modelcontextprotocol/server-github)** - GitHub integration for AI-powered development
- **[Context7 MCP Server](https://mcp.context7.com/)** - Library documentation and code examples access

### Logging & Monitoring
- **[Kermit](https://github.com/touchlab/Kermit)** (2.0.8) - Multiplatform logging
- **[ConfigCat](https://configcat.com/)** (5.1.0) - Feature flags and configuration

### Utilities
- **[Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines)** (1.10.2) - Asynchronous programming
- **[SLF4J](https://www.slf4j.org/)** (2.0.17) - Logging facade

## 🏛️ Architecture

This template follows a clean, scalable architecture:

### **MVVM + MVI Pattern**
- **Model**: Data and business logic
- **View**: UI components (Compose/SwiftUI)
- **ViewModel**: State management and business logic
- **Intent**: User actions and system events
- **State**: UI state representation

### **Component-Based Navigation**
- Uses Decompose for navigation and component lifecycle
- Each screen is a separate component
- Shared navigation logic between platforms

### **Dependency Injection**
- Koin for dependency injection
- Shared dependencies in the common module
- Platform-specific implementations

## 🔧 Configuration

### Android Configuration
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 14)
- **Compile SDK**: 36
- **Java Version**: 17
- **Kotlin JVM Target**: 17

### iOS Configuration
- **Deployment Target**: iOS 18.2+
- **Swift Version**: 5.0
- **Xcode Version**: 16.0+

### Shared Configuration
- **Kotlin Version**: 2.2.10
- **Compose Compiler**: Latest
- **Coroutines**: 1.10.2

## 🧪 Testing

### Running Tests
```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :shared:test
./gradlew :composeApp:test

# Run tests with coverage
./gradlew testDebugUnitTestCoverage
```

For detailed testing information, see [Unit Tests Shared](docs/UNIT_TESTS_SHARED.md) and [Code Coverage Reports](docs/CODE_COVERAGE_REPORTS.md).

## 🎨 Code Quality

### Code Formatting
```bash
# Format all code
./gradlew ktfmtFormat

# Check formatting without changes
./gradlew ktfmtCheck
```

### Static Analysis
```bash
# Run detekt on all modules
./gradlew detektAll

# Run on specific module
./gradlew :shared:detekt
./gradlew :composeApp:detekt
```

### Pre-commit Hooks
The project includes pre-commit hooks that automatically:
- Format code with ktfmt
- Run detekt analysis
- Block commits with quality issues

**Setup:**
```bash
pip install pre-commit
pre-commit install
```

For detailed information, see [Kotlin Format & Lint](docs/KOTLIN_FORMAT_LINT.md), [Swift Format & Lint](docs/SWIFT_FORMAT_LINT.md), and [Pre-Commit Hooks](docs/PRE_COMMIT_HOOKS.md).

### Continuous Integration
The project includes fully configurable GitHub Actions workflows that automatically:
- Run code quality checks on every PR
- Execute unit tests and coverage analysis
- Build and deploy Android and iOS apps
- Validate code formatting and linting

**Key Workflows:**
- **Shared Test & Lint** - Code quality and testing on PRs
- **Android Deploy** - Build, test, and deploy to Play Store
- **iOS Deploy** - Build, test, and deploy to TestFlight

**Automated PR Reviews:**
- **Danger Kotlin** - Configurable PR validation and quality checks
- **Automatic label suggestions** based on modified files
- **Release notes validation** for Android deployments
- **PR size and description** quality enforcement

For detailed information and customization, see [GitHub Actions Workflows](docs/GITHUB_ACTIONS.md) and [Pull Request Checks](docs/PR_DANGER_CHECKS.md).

### AI-Powered Development
The project includes MCP (Model Context Protocol) servers that enhance AI assistance:
- **GitHub Integration** - Access repository information, issues, and workflows
- **Library Documentation** - Quick access to library docs and code examples
- **Enhanced AI Context** - Better understanding of project structure and dependencies

For setup and configuration, see [MCP Servers Configuration](docs/MCP_SERVERS.md).

## 📦 Building

### Android Build
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Bundle for Play Store
./gradlew bundleRelease
```

### iOS Build
- Open `iosApp/Template.xcodeproj` in Xcode
- Select target device/simulator
- Build using `Cmd + B` or Product → Build

### Shared Framework
```bash
# Build shared framework for iOS
./gradlew :shared:assembleXCFramework
```

## 🚀 Deployment

### Android
1. Update version in `composeApp/build.gradle.kts`
2. Build release APK or bundle
3. Sign with your release keystore
4. Upload to Google Play Console

For detailed deployment guide, see [Deploy Android Version](docs/DEPLOY_ANDROID.md).

### iOS
1. Update version in Xcode project
2. Archive the project
3. Upload to App Store Connect

For detailed deployment guide, see [Deploy iOS Version](docs/DEPLOY_IOS.md).

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

If you encounter any issues or have questions:

1. Check the [Issues](../../issues) page
2. Create a new issue with detailed information
3. Include your environment details and error logs

## 🔗 Useful Links

- [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Decompose Documentation](https://arkivanov.github.io/Decompose/)
- [MVIKotlin Documentation](https://arkivanov.github.io/MVIKotlin/)
- [Koin Documentation](https://insert-koin.io/)

---

**Happy coding! 🎉**