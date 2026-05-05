# Navigation showcase

This template includes a domain-neutral navigation showcase that demonstrates
how to keep navigation state and non-UI behavior in the shared Kotlin module
while rendering native UI on Android and iOS.

Android uses Compose UI. iOS uses SwiftUI. Both platforms observe the same
Decompose components and call the same shared navigation methods.

## Covered navigation models

Decompose provides five predefined navigation models and one lower-level
generic model. This template includes examples for all of them.

- `Child Stack` manages push and pop flows.
- `Child Slot` manages one optional active child, such as a modal or overlay.
- `Child Pages` manages a list of pages with one selected page.
- `Child Panels` manages one required main panel plus optional details and
  extra panels.
- `Child Items` manages an arbitrary list of child components.
- `Generic Navigation` uses `children(...)` for custom navigation state when
  the predefined models don't fit.

The template also includes a shared deep-link router. Deep links are not a
separate Decompose navigation model. They are a recipe that parses incoming
platform links and delegates to shared navigation actions.

<!-- prettier-ignore -->
> [!NOTE]
> `Child Panels` is marked experimental by Decompose. The template opts in
> explicitly where it uses the panels API.

## Where the examples live

The shared module owns navigation configuration, component lifecycle, and
navigation actions. Platform modules own only native rendering and platform
event entry points.

- Shared component contract:
  `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/main/examples/ExamplesComponent.kt`
- Shared component implementation:
  `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/main/examples/DefaultExamplesComponent.kt`
- Shared preview component:
  `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/main/examples/PreviewExamplesComponent.kt`
- Shared tests:
  `shared/src/commonTest/kotlin/com/adriandeleon/kmp/template/main/examples/DefaultExamplesComponentTest.kt`
- Android UI:
  `androidApp/src/main/kotlin/com/adriandeleon/kmp/template/main/examples/ExamplesView.kt`
- iOS UI:
  `iosApp/KMP-Template/MainFlow/ExamplesView.swift`

## Root flow

The app starts with a root state gate. The root component decides whether to
show onboarding, authentication, or the main home flow.

Use this pattern when your app has launch-time routing that depends on
preferences, session state, feature flags, or account setup.

- `RootComponent` exposes a `Child Slot`.
- `DefaultRootComponent` activates onboarding, auth, or main.
- `DataStoreAppStateRepository` persists the app state in KMP DataStore.

To customize the root flow, replace the state checks in
`DefaultRootComponent` and keep the public root API small. Android and iOS
must not duplicate the launch decision.

## Child Pages

The template uses `Child Pages` in two places.

The onboarding flow uses pages for a linear first-run experience. The main
flow uses pages as the shared state behind the native tab UI.

- Onboarding pages:
  `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/onboarding/DefaultOnboardingComponent.kt`
- Main tabs:
  `shared/src/commonMain/kotlin/com/adriandeleon/kmp/template/main/DefaultMainComponent.kt`

Use pages when the user switches among peer destinations and only one
destination is selected at a time.

## Child Stack

The auth flow and examples flow use `Child Stack`.

The auth flow demonstrates a common sign-in stack with sign-up, forgot
password, and verification routes. The examples flow uses stack navigation to
open a neutral detail route for a selected item.

Use stack navigation when the user moves forward into a route and can return
with back navigation.

## Child Slot

The root flow, auth flow, and examples flow use `Child Slot`.

Slots fit optional children such as modals, dialogs, terms screens, and
single replacement flows. Only one slot child is active at a time.

Use a slot when the child is optional and must be easy to activate, dismiss,
or replace.

## Child Items

The examples flow uses `Child Items` to keep independent child components for
a dynamic list of neutral sample items.

Use items when a list can grow, shrink, or lazily instantiate child
components. Each child item keeps its own state and lifecycle.

## Child Panels

The examples flow uses `Child Panels` for a main-details-extra layout. The UI
lets you switch between single, dual, and triple panel modes.

Use panels for adaptive layouts that need a stable main panel and optional
secondary content. On phones, this can behave like single-pane navigation. On
larger screens, it can show multiple panes at the same time.

## Generic Navigation

The examples flow includes a small generic workspace model backed by
Decompose `children(...)`.

Use Generic Navigation only when the predefined models don't match your
state shape. The template models an arbitrary set of panes to show the
minimum pieces:

- A serializable navigation state.
- Child navigation states with explicit lifecycle statuses.
- Navigation events that transform the current state into the next state.
- A state mapper that exposes a small native-friendly UI state.

Keep Generic Navigation behind a narrow component API. Android and iOS call
methods such as `addWorkspacePane`, `activateWorkspacePane`, and
`closeWorkspacePane`; they don't manipulate the generic navigation state
directly.

## Deep links

The examples flow includes `handleDeepLink(url:)` in shared Kotlin. Platform
code can pass an incoming Android intent URL or iOS universal link URL into
that method.

The shared handler currently supports these neutral sample routes:

- `template://examples/item/sample-3`
- `template://examples/panel/sample-2`
- `template://examples/workspace/pane-2`
- `template://examples/confirmation`

Use this pattern to keep parsing and route resolution in shared code. Keep
platform-specific URL registration in Android and iOS app configuration.

## Web navigation

This template intentionally doesn't include a web navigation sample. The
template supports native Android and native iOS apps only.

If you add a web target later, document it separately and choose the routing
model that fits that target. Don't add placeholder web code to the native-only
template.

## Extend the showcase

Use the existing examples as replaceable patterns, not as product features.
The names are intentionally generic so new apps can remove or rename them
without carrying a sample domain.

To add a new navigation scenario:

1. Add the public API to the relevant shared component interface.
2. Add a failing shared unit test that describes the navigation behavior.
3. Implement the Decompose navigation model in the default component.
4. Update the preview component so Android previews and SwiftUI previews keep
   working.
5. Render the new state in native Android and iOS UI.
6. Localize every visible string in Android and iOS resources.
7. Run shared tests, Android compilation, and iOS compilation.

## Agent implementation checklist

AI coding agents must treat this file and `AGENTS.md` as required context
before adding screens, navigation routes, or deep links. The safest workflow is
shared first, native UI second.

Use this checklist for agentic implementation:

1. Identify the navigation model from the covered patterns above.
2. Reuse the closest existing component as the implementation reference.
3. Keep route parsing, app-state decisions, and navigation transforms in
   shared Kotlin.
4. Expose small component methods for native UI, such as `openDetail`,
   `showConfirmation`, or `handleDeepLink`.
5. Avoid exposing Decompose internals to SwiftUI when a simple state field or
   method gives native code the same behavior.
6. Add shared unit tests for each route, dismissal, back action, and invalid
   deep link.
7. Add platform UI only after the shared tests describe the behavior.
8. Keep examples domain-neutral unless the app consuming the template already
   has a real domain model.

When an agent is unsure which pattern to use, prefer the most specific
predefined model. Use Generic Navigation only after ruling out Stack, Slot,
Pages, Panels, and Items.

## Verification

Run these commands after changing the showcase:

```bash
./gradlew ktfmtCheck :shared:kotest :shared:compileAndroidMain \
  :shared:compileTestKotlinIosSimulatorArm64 :androidApp:compileDebugKotlin \
  --no-daemon
```

```bash
xcodebuild -project iosApp/KMP-Template.xcodeproj \
  -scheme KMP-Template \
  -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' \
  build -quiet
```

The shared tests cover every navigation model in the examples flow. Add tests
before you change behavior.

## References

Use the official Decompose documentation when updating the template:

- [Decompose navigation overview](https://arkivanov.github.io/Decompose/navigation/overview/)
- [Child Stack overview](https://arkivanov.github.io/Decompose/navigation/stack/overview/)
- [Child Pages overview](https://arkivanov.github.io/Decompose/navigation/pages/overview/)
- [Child Panels overview](https://arkivanov.github.io/Decompose/navigation/panels/overview/)
- [Generic Navigation overview](https://arkivanov.github.io/Decompose/navigation/children/overview/)
