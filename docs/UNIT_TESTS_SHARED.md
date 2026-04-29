# Unit tests in shared Kotlin

This guide explains how testing works in the template today. It focuses on the
shared Kotlin module, the single example feature in the repository, and the iOS
Swift test target that validates the native UI contract.

The template is intentionally small. The `Posts` feature is the reference
implementation for how to test shared business logic, shared presentation
logic, and native iOS UI around one end-to-end example.

## Test layout

The project uses two test layers:

- Shared Kotlin tests in `shared/src/commonTest/kotlin`
- iOS Swift tests in `iosApp/KMP-TemplateTests`

The shared module does not currently use a dedicated `iosTest` source set for
feature tests. Shared feature behavior is covered in `commonTest`, and the iOS
UI layer is covered separately with Swift Testing and ViewInspector.

## What the template covers

The repository uses the `Posts` feature to demonstrate the expected testing
shape for real features.

- Repository and mapper tests validate data transformation and persistence flow.
- Use case tests validate business behavior.
- Component tests validate the shared presentation contract exposed to native
  UIs.
- Swift UI tests validate that the iOS `PostsView` renders shared state
  correctly and handles retry interaction.

This keeps the template focused on one understandable example instead of
spreading test patterns across multiple unrelated demo features.

## Test tools

The template uses the following libraries:

- Kotest for shared Kotlin tests
- Mokkery for mocking in shared tests
- `kotlinx-coroutines-test` for coroutine control in shared tests
- Swift Testing for iOS test structure
- ViewInspector for SwiftUI inspection in the iOS test target

## Shared test locations

Shared Kotlin tests live under `shared/src/commonTest/kotlin`. Current examples
include:

- `posts/data/repository/DefaultPostsRepositoryTest.kt`
- `posts/domain/usecase/GetPostsUseCaseTest.kt`
- `posts/presentation/mapper/PostsUiMapperTest.kt`
- `posts/presentation/DefaultPostsComponentTest.kt`

These tests are the best starting point when you add new shared features to the
template.

## iOS UI test locations

The iOS SwiftUI tests live under `iosApp/KMP-TemplateTests`. The template
currently includes:

- `Posts/PostsViewTests.swift`

That file shows how to use `PreviewPostsComponent` from the shared framework to
drive SwiftUI state-based tests without duplicating feature logic in Swift.

## Run tests

Use the commands in this section as the primary test workflow for this
repository.

### Run shared Kotlin tests

Run the shared Kotest suite with:

```bash
./gradlew :shared:kotest
```

If you want the aggregated Kotlin Multiplatform test task for the shared
module, run:

```bash
./gradlew :shared:allTests
```

### Compile shared iOS Kotlin code

Compile the shared Kotlin code for the iOS simulator with:

```bash
./gradlew :shared:compileKotlinIosSimulatorArm64
```

This is useful when you change shared APIs that are consumed by SwiftUI.

### Build the iOS app and Swift test bundle

Build the iOS app and its Swift test target with:

```bash
xcodebuild \
  -project iosApp/KMP-Template.xcodeproj \
  -scheme KMP-Template \
  -destination 'generic/platform=iOS Simulator' \
  -configuration Debug \
  build-for-testing
```

This command verifies that:

- the shared `Shared` framework is exposed to the iOS app and test target,
- the app target compiles against the current shared Kotlin API, and
- the `KMP-TemplateTests` bundle compiles successfully.

## Write new shared tests

When you add a new shared feature, keep the test structure close to the feature
structure.

1. Add repository tests for data flow and mapping.
2. Add use case tests for business rules.
3. Add component or store-facing tests for shared presentation behavior.
4. Add platform UI tests only where the native layer contains real rendering or
   interaction logic.

Follow the existing `Posts` example before introducing new test patterns.

## Write new iOS UI tests

When you add a SwiftUI screen backed by a shared component, prefer the existing
pattern:

1. Create or reuse a preview component in `shared/commonMain`.
2. Drive the SwiftUI view with shared state from that preview component.
3. Use accessibility identifiers for inspectable UI elements.
4. Verify rendering and user interaction from the Swift test target.

This keeps the Swift test layer thin and avoids reimplementing feature state in
Swift.

## Notes for template maintainers

There are two implementation details worth keeping in mind when you change the
testing setup.

<!-- prettier-ignore -->
> [!IMPORTANT]
> The iOS test target must keep the same `FRAMEWORK_SEARCH_PATHS` integration as
> the app target so `import Shared` continues to compile.

<!-- prettier-ignore -->
> [!IMPORTANT]
> MVIKotlin store messages must be dispatched from the main context on iOS. If
> you move executor logic across dispatchers, verify that UI-facing store
> updates still happen on the main thread.

## Next steps

After you understand the `Posts` tests, use the same structure when you replace
the example feature with your product-specific functionality.
