# Kotlin Multiplatform native UI template

![Compose](https://img.shields.io/badge/Compose-327CF3?style=flat&logo=android&logoColor=white)
![SwiftUI](https://img.shields.io/badge/SwiftUI-FA7343?style=flat&logo=swift&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-663399?style=flat&logo=kotlin&logoColor=white)
[![Android App Deploy](https://github.com/adriandleon/kmp-template-native-ui/actions/workflows/android_deploy.yml/badge.svg?branch=main)](https://github.com/adriandleon/kmp-template-native-ui/actions/workflows/android_deploy.yml)

This repository is a public template for Kotlin Multiplatform applications with
native UI on both platforms:

- Android uses Compose Multiplatform.
- iOS uses SwiftUI.
- Shared business logic, navigation, state, networking, persistence, analytics,
  and feature flags live in Kotlin.

The template includes one example feature, `Posts`, that demonstrates the
intended architecture end to end. It fetches remote data with Ktor, caches it
with Room, exposes state through MVIKotlin and Decompose, and renders native UI
on both platforms. Keep new template consumers focused on that single example
until they replace it with their own feature set.

## What this template includes

This template gives you a production-oriented baseline without hiding the
architecture behind generated code.

- Kotlin Multiplatform with a shared `shared` module
- Compose Multiplatform Android app in `androidApp`
- SwiftUI iOS app in `iosApp`
- Decompose for navigation and component lifecycle
- MVIKotlin for state management
- Koin for dependency injection
- Ktor for networking
- Room for shared local persistence, including iOS
- ConfigCat and Firebase Remote Config for feature flags
- Firebase Analytics and Crashlytics
- Kermit for logging
- Kotest and Mokkery for shared Kotlin tests
- Swift Testing and ViewInspector for iOS UI tests

## Supported platforms

This template currently targets the following platforms:

- Android API 26 and later
- iOS 18.2 and later

## Project structure

The repository is split into three main areas:

```text
androidApp/   Android UI and app configuration
iosApp/       SwiftUI UI, Xcode project, and iOS tests
shared/       Shared Kotlin logic, data, navigation, and state
```

The `Posts` example is intentionally small. It exists to show the contract
between the shared module and both native UIs, not to act as a starter kit for
multiple unrelated features.

## Prerequisites

Before you build the template, install the following tools:

- Android Studio or IntelliJ IDEA with Kotlin Multiplatform support
- Xcode 16 or later
- Java 21

The Gradle wrapper is included, so you do not need to install Gradle manually.

## Get started

Use the following steps to run the template locally.

1. Clone the repository.

   ```bash
   git clone <your-repo-url>
   cd kmp-template-native-ui
   ```

2. Open the project in Android Studio and let Gradle sync.

3. Configure local secrets for Firebase, Supabase, and ConfigCat. The template
   reads them from `local.properties` or environment variables. Review
   [Firebase integration](docs/FIREBASE_INTEGRATION.md),
   [Supabase integration](docs/SUPABASE_INTEGRATION.md), and
   [Feature flags integration](docs/FEATURE_FLAGS_INTEGRATION.md) before you
   run the apps.

4. Run Android from Android Studio, or assemble it from the command line.

   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

5. Open `iosApp/KMP-Template.xcodeproj` in Xcode, select a simulator, and run
   the `KMP-Template` scheme.

## Template conventions

This template is meant to be readable and easy to replace.

- Keep shared business logic in `shared`.
- Expose only the minimum public API needed by Android and iOS.
- Use the existing `Posts` feature as the reference implementation for new
  features.
- Replace template branding and package identifiers early.
- Avoid adding multiple example domains to the template. One complete example is
  easier to understand and maintain than several partial ones.

## Customize the template

When you adapt the template for a real product, start with these updates:

1. Rename the application package and bundle identifiers.
2. Replace the example `Posts` feature with your product's first real feature.
3. Update app names, icons, and Firebase/Supabase configuration.
4. Review CI, deployment, and pre-commit configuration before publishing.

## Build and test

Use these commands as the primary verification workflow for the template.

### Shared Kotlin verification

Run the shared Kotlin test suite and compilation tasks with Gradle.

```bash
./gradlew :shared:kotest
./gradlew :shared:compileKotlinIosSimulatorArm64
```

If you want the aggregated Kotlin Multiplatform test task for the shared
module, run:

```bash
./gradlew :shared:allTests
```

### Android verification

Build the Android app with:

```bash
./gradlew :androidApp:assembleDebug
```

### iOS verification

Build the iOS app and Swift test bundle from Xcode, or use `xcodebuild`:

```bash
xcodebuild \
  -project iosApp/KMP-Template.xcodeproj \
  -scheme KMP-Template \
  -destination 'generic/platform=iOS Simulator' \
  -configuration Debug \
  build-for-testing
```

This template includes a Swift test target, `KMP-TemplateTests`, with
ViewInspector-based UI coverage for the `Posts` screen.

## Documentation

Use the following docs as the main entry points after cloning the template.

- [Unit tests in shared Kotlin](docs/UNIT_TESTS_SHARED.md)
- [Code coverage reports](docs/CODE_COVERAGE_REPORTS.md)
- [Kotlin format and lint](docs/KOTLIN_FORMAT_LINT.md)
- [Swift format and lint](docs/SWIFT_FORMAT_LINT.md)
- [Pre-commit hooks](docs/PRE_COMMIT_HOOKS.md)
- [GitHub Actions workflows](docs/GITHUB_ACTIONS.md)
- [Firebase integration](docs/FIREBASE_INTEGRATION.md)
- [Feature flags integration](docs/FEATURE_FLAGS_INTEGRATION.md)
- [Supabase integration](docs/SUPABASE_INTEGRATION.md)
- [Analytics integration](docs/ANALYTICS_INTEGRATION.md)
- [Android deployment](docs/DEPLOY_ANDROID.md)
- [iOS deployment](docs/DEPLOY_IOS.md)
- [Logging in Kotlin Multiplatform](docs/LOGGING_MULTIPLATFORM.md)

## Next steps

After you can build both apps, replace the example feature, update the app
identifiers, and wire your own backend and analytics configuration before using
the template for production work.
