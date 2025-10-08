# AGENTS.md

A comprehensive guide for AI coding agents working on this Kotlin Multiplatform project with native UI (Compose Multiplatform + SwiftUI).

## Project Overview

This is a **Kotlin Multiplatform** project with **native UI** targeting Android and iOS:
- **Android**: Compose Multiplatform UI
- **iOS**: SwiftUI UI  
- **Shared**: Common Kotlin business logic
- **Architecture**: MVVM + MVI with Decompose navigation
- **DI**: Koin dependency injection
- **Testing**: Kotest with 90% coverage requirement

## Setup Commands

### Prerequisites
- **Android Studio Hedgehog** (2023.1.1+) or **IntelliJ IDEA**
- **Xcode 16.0+** (for iOS development)
- **Java 17+**
- **Gradle 8.0+**

### Initial Setup
```bash
# Clone and navigate to project
git clone <repo-url>
cd kmp-template-native-ui

# Sync Gradle (Android Studio will do this automatically)
./gradlew --refresh-dependencies

# Install pre-commit hooks (optional but recommended)
pip install pre-commit
pre-commit install
```

### Development Commands
```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :shared:test
./gradlew :composeApp:test

# Run tests with coverage (must be 90%+)
./gradlew testDebugUnitTestCoverage

# Format all code
./gradlew ktfmtFormat

# Check code formatting
./gradlew ktfmtCheck

# Run static analysis
./gradlew detektAll

# Build Android debug
./gradlew :composeApp:assembleDebug

# Build Android release
./gradlew :composeApp:assembleRelease

# Build shared framework for iOS
./gradlew :shared:assembleXCFramework
```

## Code Style & Conventions

### Kotlin Code Style (Shared Module)
- **Language**: Kotlin 2.2.10 with strict mode
- **Formatting**: Use `ktfmt` with Kotlin style guide
- **Linting**: Detekt with Compose rules
- **Imports**: Single quotes, trailing commas, remove unused imports
- **Architecture**: MVVM + MVI pattern with Decompose navigation
- **Pure Kotlin**: No platform-specific dependencies in shared module
- **Package Structure**: Organize by feature, not by layer
- **Naming Conventions**:
  - **Entities**: `*Entity.kt` (e.g., `VideoEntity.kt`)
  - **Data Sources**: `*DataSource.kt` (e.g., `VideoDataSource.kt`)
  - **Repositories**: `*Repository.kt` (e.g., `VideoRepository.kt`)
  - **Use Cases**: `*UseCase.kt` (e.g., `GetVideosByCategoryUseCase.kt`)
  - **Components**: `*Component.kt` (e.g., `HomeComponent.kt`)
  - **Stores**: `*Store.kt` (e.g., `HomeStore.kt`)
  - **Mappers**: `*Mapper.kt` (e.g., `VideoMapper.kt`)

### Android Code Style (Compose Multiplatform)
- **Language**: Kotlin for all Android development
- **UI Framework**: Jetpack Compose for all UI components
- **Composable Functions**: Follow Compose naming conventions (PascalCase)
- **State Management**: Use Compose state hoisting and unidirectional data flow
- **Material Design**: Follow Material Design 3 guidelines with proper theming
- **Testing**: Use `Modifier.testTag()` for testing
- **Previews**: Implement preview functions with `@Preview`
- **Decompose Integration**: Use `subscribeAsState()` for Decompose integration
- **Internationalization**: Extract ALL text content to `strings.xml` resource files
- **Supported Languages**: English (en), Spanish Latin America (es-r419), Portuguese Brazil (pt-rBR)

### Swift Code Style (iOS)
- **Language**: Swift 5.0+ for all iOS development
- **UI Framework**: SwiftUI only
- **Formatting**: SwiftFormat + SwiftLint
- **Architecture**: MVVM with `@Observable` view models
- **Navigation**: Decompose-based navigation
- **State Management**: Use `@Observable` for view models, not `@State` for observation
- **Internationalization**: Extract ALL text content to `Localizable.strings` files
- **Supported Languages**: English (en), Spanish Latin America (es-r419), Portuguese Brazil (pt-rBR)
- **Previews**: Every SwiftUI View MUST have at least one `#Preview` macro
- **Dependency Management**: Use Swift Package Manager (SPM) as primary tool

### File Organization
```
shared/
├── src/commonMain/kotlin/
│   ├── data/           # Data layer (repositories, data sources)
│   │   ├── datasource/
│   │   ├── mapper/
│   │   └── repository/
│   ├── domain/         # Business logic (use cases, entities)
│   │   ├── entity/
│   │   ├── repository/
│   │   └── usecase/
│   └── presentation/   # UI logic (components, stores)
│       ├── component/
│       ├── mapper/
│       └── store/
composeApp/
├── src/androidMain/kotlin/  # Android-specific UI
│   └── com/yourcompany/yourapp/
│       ├── feature_a/
│       │   ├── FeatureAScreen.kt
│       │   ├── FeatureAViewModel.kt
│       │   └── components/
│       └── common/
iosApp/
├── KMP-Template/       # iOS SwiftUI views
│   ├── Assets.xcassets/
│   ├── ContentView.swift
│   ├── KMPTemplateApp.swift
│   └── Info.plist
```

## Testing Instructions

### Running Tests
```bash
# Run all tests (required before any commit)
./gradlew test

# Run tests for specific module
./gradlew :shared:test
./gradlew :composeApp:test

# Run with coverage (must pass 90% threshold)
./gradlew testDebugUnitTestCoverage

# Run specific test class
./gradlew :shared:test --tests "com.adriandeleon.template.*Test"

# Run tests matching pattern
./gradlew :shared:test --tests "*HomeStore*"
```

### Test Requirements
- **Coverage**: Minimum 90% code coverage required
- **Framework**: Use Kotest for testing
- **Mocking**: Use Mokkery for mocking
- **Location**: Tests in `commonTest` directory
- **Naming**: Test classes end with `Test` suffix

### Test Structure
```kotlin
// Example test structure
class HomeStoreTest : FunSpec({
    describe("HomeStore") {
        it("should load data successfully") {
            // Test implementation
        }
    }
})
```

## Build & Deployment

### Android Build
```bash
# Debug build
./gradlew :composeApp:assembleDebug

# Release build (signed)
./gradlew :composeApp:assembleRelease

# Bundle for Play Store
./gradlew :composeApp:bundleRelease
```

### iOS Build
```bash
# Build shared framework
./gradlew :shared:assembleXCFramework

# Then open in Xcode
open iosApp/KMP-Template.xcodeproj
```

### CI/CD Pipeline
- **GitHub Actions**: Automated testing, building, and deployment
- **Workflows**: 
  - `shared_test_lint.yml` - Code quality and testing
  - `android_deploy.yml` - Android deployment to Play Store
  - `ios_deploy.yml` - iOS deployment to TestFlight
- **Pre-commit**: Local code quality checks

## Architecture Patterns

### MVVM + MVI Architecture
- **Model**: Data and business logic in shared module
- **View**: Platform-specific UI (Compose/SwiftUI)
- **ViewModel**: State management with MVIKotlin stores
- **Intent**: User actions and system events
- **State**: Immutable UI state representation

### Clean Architecture with Feature Modules
- Each feature follows a layered architecture: `data`, `domain`, `presentation`
- Use `*Module.kt` files for feature-specific dependency injection
- Follow the naming convention: `{FeatureName}Module.kt`

### Component Structure
```kotlin
// Store interface
interface HomeStore : Store<Intent, State, Message> {
    val state: Value<State>
    fun accept(intent: Intent)
}

// Component interface  
interface HomeComponent {
    val store: HomeStore
}
```

### Data Layer Patterns
- Use `*Entity.kt` for domain models
- Create `*DataSource.kt` for data sources
- Implement `*Repository.kt` interfaces
- Use `*Mapper.kt` for data transformations
- Follow repository pattern with data sources

### Dependency Injection
- **Framework**: Koin
- **Modules**: Feature-specific modules in shared
- **Scope**: Component-scoped dependencies
- **Testing**: Use `koin-test` for test modules
- Use `module { }` blocks for dependency definitions
- Prefer `factoryOf(::Constructor)` for factory dependencies
- Use `singleOf(::Constructor)` for singleton dependencies
- Bind interfaces to implementations using `bind<Interface>()`

### Platform Abstraction
- Use `expect/actual` for platform-specific implementations
- Place common code in `commonMain`
- Use `androidMain` for Android-specific code
- Use `iosMain` for iOS-specific code
- Export shared dependencies in iOS framework configuration

## Code Quality Requirements

### Pre-commit Checks
```bash
# These run automatically on commit
./gradlew ktfmtFormat    # Format code
./gradlew detektAll      # Static analysis
./gradlew test           # Run tests
```

### Code Coverage
- **Minimum**: 90% coverage required
- **Exclusions**: Generated code, DI modules
- **Tool**: Kover with verification rules
- **Report**: Generated in `build/reports/kover/`

### Static Analysis
- **Tool**: Detekt with Compose rules
- **Config**: `config/detekt.yml`
- **Rules**: Custom Compose-specific rules enabled
- **Threshold**: All issues must be resolved

## Platform-Specific Guidelines

### Android (Compose Multiplatform)
- **UI Framework**: Compose Multiplatform only
- **Navigation**: Decompose navigation
- **State**: Use `@Observable` view models
- **Testing**: Unit tests in `androidUnitTest`
- **Previews**: Every composable MUST have `@Preview` annotations
- **Multi-Locale Previews**: Create previews for all supported languages (en, es-r419, pt-rBR)
- **Theme Variations**: Include both light and dark theme previews
- **String Resources**: Extract ALL text content to `strings.xml` resource files
- **Accessibility**: Always provide `contentDescription` for non-text UI elements
- **Performance**: Use `remember`, `derivedStateOf`, and `LazyColumn`/`LazyRow` for optimization
- **State Management**: Use Compose state hoisting and unidirectional data flow

### iOS (SwiftUI)
- **UI Framework**: SwiftUI only
- **Navigation**: Decompose-based navigation
- **State**: Use `@Observable` for view models, not `@State` for observation
- **Testing**: Unit tests in iOS test target
- **Previews**: Every SwiftUI View MUST have at least one `#Preview` macro
- **Multi-Locale Previews**: Create previews for all supported languages (en, es-r419, pt-rBR)
- **Theme Variations**: Include both light and dark mode previews
- **String Resources**: Extract ALL text content to `Localizable.strings` files
- **Accessibility**: Use `accessibilityLabel` and `accessibilityHint` with localized strings
- **Dependency Management**: Use Swift Package Manager (SPM) as primary tool
- **Performance**: Use `LazyVStack`, `LazyHStack`, or `LazyVGrid` for lazy loading

### Shared Module
- **Pure Kotlin**: No platform-specific dependencies
- **Architecture**: Clean architecture with feature modules
- **Testing**: Comprehensive unit tests required
- **Coverage**: 90% minimum coverage
- **Platform Abstraction**: Use `expect/actual` for platform-specific implementations
- **Business Logic**: Implement as use cases in domain layer
- **Data Management**: Use Ktor for HTTP requests, Kotlinx Serialization for JSON
- **Concurrency**: Use Kotlin coroutines for asynchronous operations

## Security & Configuration

### Environment Variables
```bash
# Required for local development
SUPABASE_KEY_DEV=your_dev_key
SUPABASE_URL_DEV_AND=your_android_url
SUPABASE_URL_DEV_IOS=your_ios_url
CONFIGCAT_AND_TEST_KEY=your_test_key
CONFIGCAT_IOS_TEST_KEY=your_ios_test_key
```

### Build Configuration
- **BuildKonfig**: Environment-specific configuration
- **Secrets**: Stored in `local.properties` or environment
- **Flavors**: Debug/Release configurations
- **Targets**: Android/iOS specific settings

## Preview Requirements

### Android Compose Previews
- **Mandatory**: Every composable function MUST have at least one `@Preview` annotation
- **Multi-Locale**: Create previews for all supported languages (en, es-r419, pt-rBR)
- **Theme Variations**: Include both light and dark theme previews
- **Preview Structure**:
  ```kotlin
  @Preview(name = "Login - Light - EN", locale = "en")
  @Preview(name = "Login - Light - ES", locale = "es-r419")
  @Preview(name = "Login - Light - PT", locale = "pt-rBR")
  @Composable
  fun LoginScreenLightPreview() {
      LoginScreen()
  }
  
  @Preview(name = "Login - Dark - EN", locale = "en", uiMode = Configuration.UI_MODE_NIGHT_YES)
  @Preview(name = "Login - Dark - ES", locale = "es-r419", uiMode = Configuration.UI_MODE_NIGHT_YES)
  @Preview(name = "Login - Dark - PT", locale = "pt-rBR", uiMode = Configuration.UI_MODE_NIGHT_YES)
  @Composable
  fun LoginScreenDarkPreview() {
      LoginScreen()
  }
  ```

### iOS SwiftUI Previews
- **Mandatory**: Every SwiftUI View MUST have at least one `#Preview` macro
- **Multi-Locale**: Create previews for all supported languages (en, es-r419, pt-rBR)
- **Theme Variations**: Include both light and dark mode previews
- **Preview Structure**:
  ```swift
  #Preview("Login Screen - English") {
      LoginScreen()
          .environment(\.locale, .init(identifier: "en"))
  }
  
  #Preview("Login Screen - Spanish") {
      LoginScreen()
          .environment(\.locale, .init(identifier: "es-r419"))
  }
  
  #Preview("Login Screen - Portuguese") {
      LoginScreen()
          .environment(\.locale, .init(identifier: "pt-rBR"))
  }
  ```

## Internationalization Requirements

### Android Localization
- **String Resources**: Extract ALL text content to `strings.xml` resource files
- **Resource Structure**:
  ```
  res/
  ├── values/strings.xml          # Default (English)
  ├── values-es-r419/strings.xml  # Spanish Latin America
  └── values-pt-rBR/strings.xml   # Portuguese Brazil
  ```
- **Content Types to Localize**: UI labels, buttons, error messages, accessibility descriptions, placeholders, tooltips, navigation labels, form validation messages
- **Naming Convention**: Use descriptive, hierarchical keys (e.g., `feature_login_button_text`, `error_network_unavailable`)

### iOS Localization
- **String Resources**: Extract ALL text content to `Localizable.strings` files
- **Resource Structure**:
  ```
  iosApp/
  ├── en.lproj/Localizable.strings     # English (default)
  ├── es-r419.lproj/Localizable.strings # Spanish Latin America
  └── pt-rBR.lproj/Localizable.strings  # Portuguese Brazil
  ```
- **Content Types to Localize**: UI labels, buttons, error messages, accessibility labels/hints, placeholders, tooltips, navigation labels, form validation messages, alert and action sheet texts
- **Implementation**: Always use `NSLocalizedString(key, comment: "")` for text

## Common Tasks

### Adding New Features
1. Create feature module in `shared/src/commonMain/kotlin/`
2. Implement data, domain, and presentation layers
3. Add Koin module for dependency injection
4. Create unit tests with 90%+ coverage
5. Add UI components in platform-specific modules
6. Create previews for all supported languages and themes
7. Extract all text content to localization files

### Modifying Existing Code
1. Run tests to ensure no regressions
2. Update tests if behavior changes
3. Maintain code coverage above 90%
4. Follow existing architecture patterns
5. Update documentation if needed
6. Update previews if UI changes
7. Update localization files if text changes

### Debugging Issues
1. Check test failures first
2. Run detekt for static analysis issues
3. Verify code coverage meets requirements
4. Check platform-specific logs
5. Use appropriate debugging tools per platform
6. Test in all supported languages
7. Verify previews work correctly

## Error Handling

### Common Issues
- **Test Failures**: Fix failing tests before proceeding
- **Coverage Issues**: Add tests to meet 90% threshold
- **Detekt Issues**: Resolve all static analysis warnings
- **Build Failures**: Check dependencies and configuration
- **Platform Issues**: Verify platform-specific setup

### Debug Commands
```bash
# Check test results
./gradlew test --info

# Check coverage report
open build/reports/kover/html/index.html

# Check detekt report
open build/reports/detekt/detekt.html

# Clean and rebuild
./gradlew clean build
```

## Documentation

### Key Documentation Files
- `README.md` - Project overview and setup
- `docs/` - Comprehensive documentation
- `AGENTS.md` - This file for AI agents
- `config/detekt.yml` - Static analysis configuration

### Code Documentation
- Use KDoc for public APIs
- Document complex business logic
- Include usage examples
- Maintain architecture documentation

---

**Remember**: Always run `./gradlew test` before committing. This project requires 90% test coverage and all quality checks must pass.
