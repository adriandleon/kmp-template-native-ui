# Native deeplinks

Native modules capture incoming links. The shared module interprets links and
turns them into Decompose navigation. This keeps platform setup native while
preserving one routing implementation for Android and iOS.

Use this rule for every deeplink feature:

> Native code captures links; shared code interprets links.

## Ownership boundary

Android and iOS own platform-specific link registration and lifecycle events.
They don't parse app routes or choose app screens.

Native modules own:

- Android intent filters in `AndroidManifest.xml`.
- iOS URL schemes, universal links, and associated domains.
- Extracting the incoming platform URL.
- Passing the raw URL string into the public shared root component.

The shared module owns:

- URL normalization.
- Route parsing.
- Validation and fallback behavior.
- State-gated routing, such as onboarding or authentication requirements.
- Mapping routes to Decompose navigation actions.
- Unit tests for valid, invalid, nested, and gated routes.

## Public root API

Expose one narrow entry point from shared code. Prefer a raw string because it
keeps platform URL types out of the shared public API.

```kotlin
interface RootComponent {
    fun handleDeepLink(url: String): Boolean
}
```

The return value lets platform code log or inspect whether shared code accepted
the link. Platform code must not use the return value to perform additional app
navigation.

## Android capture

Android captures the URL from the Activity intent and forwards it to shared
code. It doesn't inspect path segments or query parameters.

```kotlin
private fun handleIntent(intent: Intent) {
    val url = intent.dataString ?: return
    rootComponent.handleDeepLink(url)
}
```

For cold starts, pass the initial URL to root creation or call
`handleDeepLink(url)` after the root component is created. For warm starts,
override `onNewIntent`, update the Activity intent, and forward the new URL to
the same root component.

## iOS capture

iOS captures the URL through SwiftUI and forwards it to shared code. It doesn't
inspect path components or query items.

```swift
WindowGroup {
    RootView(rootComponent)
        .onOpenURL { url in
            rootComponent.handleDeepLink(url: url.absoluteString)
        }
}
```

Use native app configuration for URL schemes and universal links. Keep the
route table in shared Kotlin.

## Shared routing

Shared code validates and routes the URL. It can delegate to child components
when a route belongs to a nested flow.

```kotlin
internal class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {
    override fun handleDeepLink(url: String): Boolean {
        val route = DeepLinkRoute.parse(url) ?: return false
        return when (route) {
            is DeepLinkRoute.Examples -> openExamples(route)
            is DeepLinkRoute.Auth -> openAuth(route)
        }
    }
}
```

Keep parser types, route models, and route-to-navigation transforms internal.
Native code sees only `handleDeepLink(url)`.

## Forbidden native routing

Do not parse app routes in Android UI:

```kotlin
if (url.contains("examples/detail")) {
    component.openDetail("sample-1")
}
```

Do not parse app routes in SwiftUI:

```swift
if url.pathComponents.contains("auth") {
    // Choose a screen here.
}
```

Both examples duplicate shared routing logic and couple native UI to app
navigation internals.

## Tests

Shared tests must cover route behavior because shared code owns the route
table. Add tests for:

- A valid route that opens the expected child.
- A nested route that delegates to a child flow.
- An invalid route that returns `false` and keeps current navigation stable.
- A gated route that respects onboarding, authentication, or app setup state.

Platform tests only verify capture and forwarding examples. They don't need to
cover every route. The template uses platform UI tests as examples for agents
to expand, not as complete end-to-end route coverage.

## Agent checklist

Use this checklist when you add a deeplink:

1. Add or update native platform registration only when the URL scheme, host,
   or associated domain changes.
2. Keep native code limited to URL capture and forwarding.
3. Add shared tests for route parsing and Decompose navigation behavior.
4. Keep parser and route models internal to shared code.
5. Expose new renderable result state through `Component.UiState` when native
   UI needs to show deeplink status.
6. Update Android and iOS examples only to demonstrate capture or rendering,
   not to duplicate route decisions.
