# KMP Template Improvements Design

**Date:** 2026-04-29
**Status:** Approved
**Approach:** Infrastructure-first (B) — fixes → Room → localization → example feature → UI tests

---

## Overview

A structured set of improvements to the KMP template across five areas, executed in dependency order so each phase builds cleanly on the previous one.

**Improvement areas (in execution order):**
1. Quick build fixes
2. Room KMP integration (Koin-provided, platform-specific builders)
3. Localization scaffold (en, es-r419, pt-rBR on Android and iOS)
4. Example feature — Posts (full end-to-end with strict API boundary)
5. UI tests — Android Robot pattern + iOS Swift Testing

**Out of scope (noted for future consideration):**
- Gradle convention plugins (`build-logic` included build) — validated separately after quick fixes
- Automated dependency updates (Renovate / Dependabot) — current manual cadence is sufficient

---

## Section 1: Quick Build Fixes

### 1.1 Firebase BOM → version catalog

**Problem:** `shared/build.gradle.kts` hardcodes `"com.google.firebase:firebase-bom:33.9.0"` in `androidMain.dependencies`.

**Fix:**
- Add `firebase-bom = "33.9.0"` to `[versions]` in `libs.versions.toml`
- Add `firebase-bom = { module = "com.google.firebase:firebase-bom", version.ref = "firebase-bom" }` to `[libraries]`
- Replace the hardcoded string in `androidMain.dependencies` with `platform(libs.firebase.bom)`

### 1.2 MVIKotlin debug-only tools — runtime gating

**Problem:** `mvikotlin-logging` and `mvikotlin-timetravel` are development-only tools included unconditionally.

**Constraint:** KMP shared modules do not support `debugImplementation` in `commonMain`, so the gate must be in code not Gradle.

**Fix:** In `CommonModule.kt` (Koin), provide the `StoreFactory` conditionally:
```kotlin
single<StoreFactory> {
    if (BuildKonfig.DEBUG) {
        LoggingStoreFactory(DefaultStoreFactory())
    } else {
        DefaultStoreFactory()
    }
}
```
`TimeTravelStoreFactory` is similarly gated. Dependencies remain in `commonMain` but are never activated in release builds.

### 1.3 BuildKonfig `iosSimulatorArm64` deduplication

**Problem:** `targetConfigs` and `targetConfigs("release")` blocks each repeat identical config for `iosArm64` and `iosSimulatorArm64`.

**Fix:** Iterate over both targets in a single block:
```kotlin
targetConfigs {
    listOf("iosArm64", "iosSimulatorArm64").forEach { target ->
        create(target) {
            buildConfigField(STRING, "SUPABASE_URL", getSecret("SUPABASE_URL_DEV_IOS"))
            buildConfigField(STRING, "CONFIGCAT_KEY", getSecret("CONFIGCAT_IOS_TEST_KEY"))
        }
    }
}
```
Same pattern applied to `targetConfigs("release")`.

### 1.4 Setup script cross-platform fix

**Problem:** `setup_new_project.sh` uses `tail -r` (macOS-only, not available in GNU coreutils) which breaks on Linux CI runners.

**Fix:** Replace `tail -r` with a POSIX-compatible `awk` equivalent:
```bash
# Before (macOS-only):
echo "$1" | tr '.' '\n' | tail -r | tr '\n' '.' | sed 's/\.$//'

# After (POSIX-compatible):
echo "$1" | tr '.' '\n' | awk '{lines[NR]=$0} END {for(i=NR;i>=1;i--) printf "%s%s", lines[i], (i>1 ? "." : "\n")}'
```

---

## Section 2: Room KMP Integration

Room 2.7.0+ has official KMP support. All Room code lives in `shared` and is `internal`.

### 2.1 Version catalog additions

```toml
[versions]
room = "2.7.1"

[libraries]
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }

[plugins]
room = { id = "androidx.room", version.ref = "room" }
```

### 2.2 `shared/build.gradle.kts` changes

- Add `alias(libs.plugins.room)` to the plugins block (KSP plugin already present)
- Add `room-runtime` to `commonMain.dependencies`
- Add `ksp(libs.room.compiler)` for each KMP target:
  - `add("kspAndroid", libs.room.compiler)`
  - `add("kspIosArm64", libs.room.compiler)`
  - `add("kspIosSimulatorArm64", libs.room.compiler)`
- Configure schema output:
```kotlin
room {
    schemaDirectory("$projectDir/schemas")
}
```

### 2.3 Database structure (all `internal`)

```
shared/src/commonMain/kotlin/.../db/
├── AppDatabase.kt          — @Database abstract class
└── post/
    ├── PostEntity.kt       — @Entity data class
    └── PostDao.kt          — @Dao interface with suspend queries
```

### 2.4 Platform-specific database builder via `expect/actual`

```kotlin
// commonMain
internal expect fun createAppDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase

// androidMain
internal actual fun createAppDatabase(builder: RoomDatabase.Builder<AppDatabase>) =
    builder.build()

// iosMain
internal actual fun createAppDatabase(builder: RoomDatabase.Builder<AppDatabase>) =
    builder.build()
```

### 2.5 Koin platform modules

Platform-specific Koin modules provide the database builder, keeping platform context out of `commonMain`:

```kotlin
// androidMain — AndroidModule.kt
single<AppDatabase> {
    createAppDatabase(
        Room.databaseBuilder<AppDatabase>(get<Context>(), "app.db")
    )
}

// iosMain — IosModule.kt
single<AppDatabase> {
    createAppDatabase(
        Room.databaseBuilder<AppDatabase>(
            name = NSHomeDirectory() + "/app.db"
        )
    )
}
```

Both modules are included in `KoinApp` initialization alongside the existing modules.

---

## Section 3: Localization Scaffold

### 3.1 Android

Three resource directories under `androidApp/src/main/res/`:

```
values/strings.xml            ← English (default, required fallback)
values-es-r419/strings.xml    ← Spanish Latin America
values-pt-rBR/strings.xml     ← Portuguese Brazil
```

Each file includes strings for the Posts feature: screen title, loading description, error message, retry button label, empty state message.

Header comment in each file:
```xml
<!-- Localization note:
     - English (values/) is the fallback for all unsupported locales.
     - To add a new language: duplicate this folder and change the locale suffix (e.g. values-fr/).
     - To remove a language: delete its folder.
     - To add more languages: see https://developer.android.com/guide/topics/resources/localization
-->
```

### 3.2 iOS

Three `.lproj` directories under `iosApp/KMP-Template/`:

```
en.lproj/Localizable.strings
es-419.lproj/Localizable.strings
pt-BR.lproj/Localizable.strings
```

Each file includes the same Posts feature strings. Header comment:
```
// Localization note:
// - en.lproj is the fallback for all unsupported locales.
// - To add a language: duplicate this folder and change the locale code (e.g. fr.lproj/).
// - To remove a language: delete its folder.
// - To add more languages: see https://developer.apple.com/documentation/xcode/localization
```

---

## Section 4: Example Feature — Posts

A Posts list that fetches from a remote API (Ktor), caches locally (Room), and displays on both platforms. Demonstrates the full MVI stack with a strict API boundary enforced at the Kotlin visibility level.

### 4.1 Public API surface

Only these types are visible to `androidApp` and `iosApp`:

```kotlin
// PostsComponent.kt — public
interface PostsComponent {
    val state: Value<PostsUiState>
    fun onRetry()
}

// PostsUiState.kt — public
sealed class PostsUiState {
    data object Loading : PostsUiState()
    data class Content(val posts: List<PostUiModel>) : PostsUiState()
    data class Error(val message: String) : PostsUiState()
}

// PostUiModel.kt — public
data class PostUiModel(
    val id: String,
    val title: String,
    val body: String,
)
```

Everything else in the feature is `internal` or `private`.

### 4.2 Internal shared module structure

```
shared/src/commonMain/kotlin/.../posts/
├── data/
│   ├── datasource/
│   │   ├── PostsRemoteDataSource.kt    ← Ktor HTTP call (internal)
│   │   └── PostsLocalDataSource.kt     ← Room DAO wrapper (internal)
│   ├── mapper/
│   │   └── PostEntityMapper.kt         ← PostDto ↔ PostEntity (internal)
│   └── repository/
│       └── DefaultPostsRepository.kt   ← implements PostsRepository (internal)
├── domain/
│   ├── model/
│   │   └── Post.kt                     ← domain model (internal)
│   ├── repository/
│   │   └── PostsRepository.kt          ← interface (internal)
│   └── usecase/
│       └── GetPostsUseCase.kt          ← returns Flow<List<Post>> (internal)
├── presentation/
│   ├── mapper/
│   │   └── PostsUiMapper.kt            ← Post → PostUiModel (internal)
│   ├── store/
│   │   ├── PostsStore.kt               ← Store<Intent, State, Label> (internal)
│   │   ├── PostsStoreFactory.kt        ← wires Executor + Reducer (internal)
│   │   ├── PostsIntent.kt              ← LoadPosts, Retry (internal)
│   │   ├── PostsState.kt               ← isLoading, posts, error (internal)
│   │   └── PostsMessage.kt             ← PostsLoaded, PostsFailed (internal)
│   └── DefaultPostsComponent.kt        ← maps State → PostsUiState (internal)
└── PostsModule.kt                      ← Koin bindings (internal)
```

### 4.3 Data flow

```
UI → PostsComponent.onRetry()
   → PostsStore.accept(Intent.Retry)
   → Executor → GetPostsUseCase
               → PostsRepository
                 → PostsRemoteDataSource (Ktor) → write → PostsLocalDataSource (Room)
                 → PostsLocalDataSource (Room) ← read
   → Reducer → PostsState (internal)
   → DefaultPostsComponent maps PostsState → PostsUiState via PostsUiMapper
   → UI observes Value<PostsUiState> (public)
```

### 4.4 API boundary enforcement

`DefaultPostsComponent` is the single choke point between MVI internals and the UI:
- It holds the `PostsStore` reference (`private`)
- It subscribes to `PostsStore.state` and maps it to `PostsUiState` via `PostsUiMapper`
- It exposes only `Value<PostsUiState>` and `onRetry()` (from the `PostsComponent` interface)
- Platform UI modules only import `PostsComponent`, `PostsUiState`, and `PostUiModel`

No `PostsState`, `PostsStore`, `PostsIntent`, `Post`, `PostEntity`, or any internal type is ever visible outside `shared`.

### 4.5 Android UI — `androidApp`

`PostsView.kt`: a `@Composable` function that:
- Receives `PostsComponent`
- Subscribes via `component.state.subscribeAsState()`
- Renders each `PostsUiState` branch with `testTag` using string resource IDs (for UI test targeting)
- Uses only public types: `PostsComponent`, `PostsUiState`, `PostUiModel`
- Includes `@Preview` annotations for Loading, Content, and Error states in all 3 locales and both themes

### 4.6 iOS UI — `iosApp`

`PostsView.swift`: a SwiftUI view that:
- Receives `PostsComponent`
- Observes state via `ObservableValue`
- Renders each `PostsUiState` branch with `.accessibilityIdentifier()` (for test targeting)
- Uses only public types: `PostsComponent`, `PostsUiState`, `PostUiModel`
- Includes `#Preview` macros for Loading, Content, and Error states in all 3 locales and both color schemes

### 4.7 Navigation

`DefaultRootComponent` gains a Posts child. Both `RootView.kt` and `RootView.swift` are updated to render the Posts screen as the initial destination.

### 4.8 Koin wiring

Decompose components are NOT created by Koin — they are instantiated by their parent component via `childStack`, receiving a `ComponentContext`. Koin provides store-level dependencies only.

`PostsModule.kt` (internal):
```kotlin
internal val postsModule = module {
    factoryOf(::PostsRemoteDataSource)
    factoryOf(::PostsLocalDataSource)
    factoryOf(::PostEntityMapper)
    factoryOf(::DefaultPostsRepository) bind PostsRepository::class
    factoryOf(::GetPostsUseCase)
    factoryOf(::PostsUiMapper)
    factoryOf(::PostsStoreFactory)
    // DefaultPostsComponent is NOT registered here — it is created by DefaultRootComponent
}
```

`DefaultRootComponent` creates `DefaultPostsComponent` via constructor injection, retrieving `PostsStoreFactory` from Koin:
```kotlin
private fun postsComponent(componentContext: ComponentContext): PostsComponent =
    DefaultPostsComponent(
        componentContext = componentContext,
        storeFactory = get<PostsStoreFactory>(),
    )
```

`DefaultRootComponent` gains access to Koin by implementing `KoinComponent`, which is already the pattern in this project.

`PostsModule` is included in `KoinApp` alongside existing modules.

---

## Section 5: UI Tests

### 5.1 Android — Robot Pattern

**Location:** `androidApp/src/androidTest/`

**Structure:**
```
androidTest/
├── util/
│   └── TestUtils.kt                   ← onNodeWithTag/Text/ContentDesc helpers
└── posts/
    ├── PreviewPostsComponent.kt        ← fake component, MutableValue<PostsUiState>
    ├── PostsViewRobot.kt               ← launchPostsView(), Robot, Verification
    └── PostsViewTest.kt                ← test cases using robot DSL
```

**`TestUtils.kt`** — resolves string resources from the instrumentation context:
```kotlin
fun SemanticsNodeInteractionsProvider.onNodeWithTag(@StringRes resId: Int): SemanticsNodeInteraction
fun SemanticsNodeInteractionsProvider.onNodeWithText(@StringRes resId: Int): SemanticsNodeInteraction
fun SemanticsNodeInteractionsProvider.onNodeWithContentDescription(@StringRes resId: Int): SemanticsNodeInteraction
```

**`PreviewPostsComponent`** — a fake `PostsComponent` with a `MutableValue<PostsUiState>` so tests control state independently of any MVI internals.

**`PostsViewRobot.kt`** — three-class pattern:
```kotlin
fun ComposeContentTestRule.launchPostsView(
    component: PreviewPostsComponent,
    block: PostsViewRobot.() -> Unit = {},
): PostsViewRobot

class PostsViewRobot { infix fun verify(block: PostsViewVerification.() -> Unit) }
class PostsViewVerification { /* assertions */ }
```

**`PostsViewTest.kt`** — reads as fluent DSL:
```kotlin
composeTestRule.launchPostsView(component) verify { loadingIndicatorIsDisplayed() }
composeTestRule.launchPostsView(component) verify { postsListIsDisplayed() }
composeTestRule.launchPostsView(component) verify { errorMessageIsDisplayed() }
composeTestRule.launchPostsView(component) { clickRetry() } verify { retryWasCalled() }
```

**What is NOT tested here (intentional scope):**
- MVI Store logic → `commonTest` unit tests
- Room queries → `PostsLocalDataSourceTest` in `commonTest`
- Navigation → `DefaultRootComponent` unit tests in `commonTest`

### 5.2 iOS — Swift Testing

**Location:** `iosApp/KMP-TemplateTests/`

**SPM dependency:** `ViewInspector` added to the test target only (no production code changes).

**Structure:**
```
KMP-TemplateTests/
├── Utils/
│   └── PreviewPostsComponent.swift    ← fake component, MutableValue<PostsUiState>
└── Posts/
    └── PostsViewTests.swift           ← Swift Testing suite
```

**`PostsViewTests.swift`:**
```swift
@MainActor
@Suite("PostsView Test Suite")
struct PostsViewTests {
    private let component = PreviewPostsComponent()
    private let sut: PostsView

    init() { sut = PostsView(component) }

    // Parameterized: covers Loading and Error states in one test
    @Test("shows correct view for non-content states", arguments: [
        (PostsUiStateLoading(), "posts_loading"),
        (PostsUiStateError(message: "Oops"), "posts_error"),
    ])
    func showsCorrectViewForState(state: PostsUiState, identifier: String) async throws { ... }

    @Test("shows posts list with correct item count when content is available")
    func showsPostsListWhenContent() async throws { ... }

    @Test("calls onRetry when retry button is tapped")
    func callsOnRetryWhenRetryTapped() async throws { ... }
}
```

**Key practices applied:**
- `@MainActor` at suite level — SwiftUI requires main-thread isolation
- `struct` suite — value semantics, no accidental shared state
- `#expect` / `#require` for assertions, never XCTest assertions
- Parameterized test for state→view-identifier mappings (avoids duplicated test functions)
- Accessibility identifiers (`.accessibilityIdentifier(...)`) for view targeting — not `.id()`
- `ViewIdentifiers` enum with string constants shared between view and test

---

## Future Considerations

- **Gradle convention plugins** (`build-logic` included build): centralize `ktfmt`, `detekt`, `kover`, `testlogger` config. Evaluate separately after quick fixes land.
- **Automated dependency updates** (Renovate or Dependabot): current manual cadence works for a template; evaluate when dependency count grows.
