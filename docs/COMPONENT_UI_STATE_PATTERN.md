# Component UI state pattern

Use `Component.UiState` as the public screen-rendering contract between the
shared module and native UI. The component owns the UI contract, the store owns
internal behavior, and platform views render state without knowing shared
implementation details.

This pattern keeps Android Compose and iOS SwiftUI coupled only to public
Decompose component interfaces.

## Naming convention

Every screen component that exposes renderable state must use this shape:

```kotlin
interface FeatureComponent {
    val uiState: Value<UiState>

    fun onPrimaryAction()

    data class UiState(
        val title: String,
        val isLoading: Boolean,
        val items: List<Item>,
    )

    data class Item(
        val id: String,
        val title: String,
        val subtitle: String,
    )
}
```

Use `UiState` as a nested type because it makes ownership explicit:

- `FeatureComponent.UiState` belongs to the component contract.
- `Store.State` belongs to MVIKotlin and stays internal.
- Domain and persistence models stay out of platform UI.
- Platform code can discover the full UI contract from the component interface.

Avoid names such as `FeatureModel`, `FeatureState`, `FeatureViewModel`, and
`FeatureViewState` in shared public APIs. They are less precise and can blur
the boundary between UI state, store state, domain state, and native view
models.

## Internal store state

MVIKotlin store state must stay internal to shared code. It can contain domain
entities, persistence flags, loading decisions, raw errors, feature flags, and
other values that native UI doesn't need to know.

```kotlin
internal interface FeatureStore : Store<Intent, State, Label> {
    data class State(
        val entities: List<FeatureEntity> = emptyList(),
        val isLoading: Boolean = false,
        val rawError: Throwable? = null,
    )
}
```

Native modules must not import store types, intents, messages, labels,
repositories, use cases, data sources, mappers, entities, or persistence
models.

## Internal mapper

Map store state to component UI state in shared code with an internal mapper.
Keep formatting, filtering, sorting, feature flag decisions, and route-derived
rendering decisions in this mapper or in other internal shared code.

```kotlin
internal fun FeatureStore.State.toUiState(): FeatureComponent.UiState =
    FeatureComponent.UiState(
        title = "Feature",
        isLoading = isLoading,
        items = entities.map { entity ->
            FeatureComponent.Item(
                id = entity.id,
                title = entity.displayTitle,
                subtitle = entity.description.orEmpty(),
            )
        },
    )
```

The default component wires the internal store to the public UI state:

```kotlin
internal class DefaultFeatureComponent(
    componentContext: ComponentContext,
    private val storeFactory: FeatureStoreFactory,
) : FeatureComponent, ComponentContext by componentContext {
    private val store = storeFactory.create()
    private val _uiState = MutableValue(store.state.toUiState())

    override val uiState: Value<FeatureComponent.UiState> = _uiState

    override fun onPrimaryAction() {
        store.accept(FeatureStore.Intent.PrimaryAction)
    }
}
```

## Platform rendering rule

Native UI renders `component.uiState` and calls component methods. It doesn't
derive business state, parse app routes, call repositories, or translate store
intents directly.

Android Compose follows this shape:

```kotlin
@Composable
fun FeatureView(component: FeatureComponent) {
    val uiState by component.uiState.subscribeAsState()

    FeatureContent(
        title = uiState.title,
        items = uiState.items,
        onPrimaryAction = component::onPrimaryAction,
    )
}
```

iOS SwiftUI follows this shape:

```swift
struct FeatureView: View {
    private let component: FeatureComponent
    @StateValue private var uiState: FeatureComponentUiState

    init(_ component: FeatureComponent) {
        self.component = component
        _uiState = StateValue(component.uiState)
    }

    var body: some View {
        FeatureContent(
            title: uiState.title,
            items: uiState.items,
            onPrimaryAction: component.onPrimaryAction
        )
    }
}
```

## Preview components

Every public component must have a `Preview*Component` test double. Previews and
platform UI tests use preview components instead of default components.

```kotlin
class PreviewFeatureComponent : FeatureComponent {
    private val _uiState = MutableValue(
        FeatureComponent.UiState(
            title = "Preview",
            isLoading = false,
            items = previewItems,
        )
    )

    override val uiState: Value<FeatureComponent.UiState> = _uiState

    override fun onPrimaryAction() = Unit
}
```

Use preview components for Android Compose previews, Android robot tests,
SwiftUI previews, and Swift Testing component tests.

## Agent checklist

Use this checklist when you add or change a screen:

1. Define or update the public `FeatureComponent` interface first.
2. Expose `val uiState: Value<FeatureComponent.UiState>` for renderable data.
3. Keep MVIKotlin stores, store state, mappers, repositories, and use cases
   internal.
4. Add internal mapper tests for `Store.State` to `Component.UiState`.
5. Render only `Component.UiState` in Android and iOS.
6. Send user actions back through component methods.
7. Update the matching `Preview*Component`.
8. Add focused platform UI tests only for examples that future agents can
   expand.
