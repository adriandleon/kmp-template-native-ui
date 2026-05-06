//
//  ExamplesView.swift
//  KMP-Template
//
//  Created by Adrian De León on 5/5/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct ExamplesView: View {
    private let component: ExamplesComponent

    @StateValue
    private var stack: ChildStack<AnyObject, ExamplesComponentChild>

    @StateValue
    private var modalSlot: ChildSlot<AnyObject, ExamplesComponentModalChild>

    init(_ component: ExamplesComponent) {
        self.component = component
        _stack = StateValue(component.stack)
        _modalSlot = StateValue(component.modalSlot)
    }

    var body: some View {
        ZStack {
            StackView(
                stackValue: _stack,
                getTitle: title(for:),
                onBack: component.backTo(index:),
            ) { child in
                switch child {
                case is ExamplesComponentChildList:
                    ExamplesListView(component: component)
                case let child as ExamplesComponentChildDetail:
                    ExamplesDetailView(component: component, detail: child.component)
                default:
                    EmptyView()
                }
            }
            .accessibilityIdentifier("examples_screen")

            if modalSlot.child?.instance is ExamplesComponentModalChildConfirmation {
                ExamplesConfirmationOverlay(component: component)
            }
        }
    }
}

private struct ExamplesListView: View {
    let component: ExamplesComponent
    @StateObject private var uiStateObserver: ObservableValue<ExamplesComponentUiState>

    init(component: ExamplesComponent) {
        self.component = component
        self._uiStateObserver = StateObject(
            wrappedValue: ObservableValue(component.uiState)
        )
    }

    var body: some View {
        let state = uiStateObserver.value

        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text("Navigation examples")
                    .font(.title2)
                    .fontWeight(.semibold)

                Text("This screen demonstrates stack, slot, and child item navigation in one neutral flow.")
                    .font(.body)
                    .foregroundStyle(.secondary)

                HStack(spacing: 12) {
                    Button("Add item", action: component.addItem)
                        .buttonStyle(.borderedProminent)
                        .accessibilityIdentifier("examples_add_button")

                    Button("Show modal", action: component.showConfirmation)
                        .buttonStyle(.bordered)
                        .accessibilityIdentifier("examples_modal_button")
                }

                ForEach(state.itemIds, id: \.self) { itemId in
                    SampleItemRow(
                        component: component.itemComponent(itemId: itemId),
                        isSelected: state.selectedItemId == itemId,
                        onSelect: { component.selectItem(itemId: itemId) },
                        onOpenDetail: { component.openDetail(itemId: itemId) },
                        onRemove: { component.removeItem(itemId: itemId) },
                    )
                }

                PanelsShowcaseView(component: component, state: state)

                GenericNavigationShowcaseView(component: component, state: state)

                DeepLinkShowcaseView(component: component, state: state)
            }
            .padding(24)
        }
    }
}

private struct SampleItemRow: View {
    let component: SampleItemComponent
    let isSelected: Bool
    let onSelect: () -> Void
    let onOpenDetail: () -> Void
    let onRemove: () -> Void

    @StateObject private var uiStateObserver: ObservableValue<SampleItemComponentUiState>

    init(
        component: SampleItemComponent,
        isSelected: Bool,
        onSelect: @escaping () -> Void,
        onOpenDetail: @escaping () -> Void,
        onRemove: @escaping () -> Void,
    ) {
        self.component = component
        self.isSelected = isSelected
        self.onSelect = onSelect
        self.onOpenDetail = onOpenDetail
        self.onRemove = onRemove
        self._uiStateObserver = StateObject(
            wrappedValue: ObservableValue(component.uiState)
        )
    }

    var body: some View {
        let state = uiStateObserver.value

        VStack(alignment: .leading, spacing: 8) {
            Text(localizedFormat("%@ · count %lld", state.title, Int64(state.count)))
            .font(.headline)

            Text(isSelected ? "Selected" : "Not selected")
            .font(.caption)
            .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button("Select", action: onSelect)
                    .buttonStyle(.bordered)
                Button("Increment", action: component.increment)
                    .buttonStyle(.bordered)
            }

            HStack(spacing: 8) {
                Button("Open detail", action: onOpenDetail)
                    .buttonStyle(.borderedProminent)
                Button("Remove", action: onRemove)
                    .buttonStyle(.plain)
            }
        }
        .padding(.vertical, 10)
        .accessibilityIdentifier("examples_item")
    }
}

private struct PanelsShowcaseView: View {
    let component: ExamplesComponent
    let state: ExamplesComponentUiState

    private var selectedItemId: String? {
        state.selectedItemId ?? state.itemIds.first
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Panels")
                .font(.title3)
                .fontWeight(.semibold)

            Text("This area demonstrates one main panel with optional details and extra panels.")
                .font(.body)
                .foregroundStyle(.secondary)

            Text(localizedFormat("Mode: %@", localizedPanelsModeName(state.panelsMode.name)))
            .font(.caption)
            .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button("Single") {
                    component.setPanelsMode(mode: .single)
                }
                .buttonStyle(.bordered)

                Button("Dual") {
                    component.setPanelsMode(mode: .dual)
                }
                .buttonStyle(.bordered)

                Button("Triple") {
                    component.setPanelsMode(mode: .triple)
                }
                .buttonStyle(.bordered)
            }

            Text("Main panel")
                .font(.headline)

            Text("The main panel remains the stable entry point for selecting a child.")
                .font(.caption)
                .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button("Open details") {
                    if let selectedItemId {
                        component.openPanelDetails(itemId: selectedItemId)
                    }
                }
                .buttonStyle(.borderedProminent)
                .disabled(selectedItemId == nil)

                Button("Open extra") {
                    if let selectedItemId {
                        component.openPanelExtra(itemId: selectedItemId)
                    }
                }
                .buttonStyle(.bordered)
                .disabled(selectedItemId == nil)
            }

            Text(panelText(
                isVisible: state.hasPanelDetails,
                format: "Details panel is showing %@.",
                empty: "Details panel is closed."
            ))
            .font(.caption)
            .foregroundStyle(.secondary)

            if state.hasPanelDetails {
                Button(
                    "Close details",
                    action: component.dismissPanelDetails
                )
                .buttonStyle(.plain)
            }

            Text(panelText(
                isVisible: state.hasPanelExtra,
                format: "Extra panel is showing more context for %@.",
                empty: "Extra panel is closed."
            ))
            .font(.caption)
            .foregroundStyle(.secondary)

            if state.hasPanelExtra {
                Button(
                    "Close extra",
                    action: component.dismissPanelExtra
                )
                .buttonStyle(.plain)
            }
        }
    }

    private func panelText(
        isVisible: Bool,
        format: LocalizedStringResource,
        empty: LocalizedStringResource,
    ) -> String {
        if isVisible {
            return localizedFormat(format, state.panelItemId ?? "")
        }

        return String(localized: empty)
    }
}

private struct GenericNavigationShowcaseView: View {
    let component: ExamplesComponent
    let state: ExamplesComponentUiState

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Generic navigation")
                .font(.title3)
                .fontWeight(.semibold)

            Text("This area uses Decompose children for a custom navigation model with an arbitrary set of panes.")
                .font(.body)
                .foregroundStyle(.secondary)

            Text(localizedFormat("Active pane: %@", state.activeWorkspacePaneId ?? ""))
            .font(.caption)
            .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                ForEach(state.workspacePaneIds, id: \.self) { paneId in
                    Button(paneId) {
                        component.activateWorkspacePane(paneId: paneId)
                    }
                    .buttonStyle(.bordered)
                }
            }

            HStack(spacing: 8) {
                Button("Add pane", action: component.addWorkspacePane)
                    .buttonStyle(.borderedProminent)

                Button("Close active") {
                    if let activePaneId = state.activeWorkspacePaneId {
                        component.closeWorkspacePane(paneId: activePaneId)
                    }
                }
                .buttonStyle(.bordered)
                .disabled(state.activeWorkspacePaneId == nil || state.workspacePaneIds.count <= 1)
            }
        }
    }
}

private struct DeepLinkShowcaseView: View {
    let component: ExamplesComponent
    let state: ExamplesComponentUiState

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Deep links")
                .font(.title3)
                .fontWeight(.semibold)

            Text("These actions simulate platform links entering shared navigation and resolving to stack, slot, panels, or generic children.")
                .font(.body)
                .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button("Item link") {
                    _ = component.handleDeepLink(url: "template://examples/item/sample-3")
                }
                .buttonStyle(.borderedProminent)

                Button("Panel link") {
                    _ = component.handleDeepLink(url: "template://examples/panel/sample-2")
                }
                .buttonStyle(.bordered)
            }

            HStack(spacing: 8) {
                Button("Pane link") {
                    _ = component.handleDeepLink(url: "template://examples/workspace/pane-2")
                }
                .buttonStyle(.bordered)

                Button("Modal link") {
                    _ = component.handleDeepLink(url: "template://examples/confirmation")
                }
                .buttonStyle(.plain)
            }

            Text(statusText)
                .font(.caption)
                .foregroundStyle(.secondary)
        }
    }

    private var statusText: String {
        guard let lastDeepLinkPath = state.lastDeepLinkPath else {
            return String(localized: "No link handled yet.")
        }

        return localizedFormat(
            "Last link: %@ · handled: %@",
            lastDeepLinkPath,
            localizedBoolean(state.lastDeepLinkHandled?.boolValue ?? false)
        )
    }
}

private struct ExamplesDetailView: View {
    let component: ExamplesComponent
    let detail: ExamplesComponentDetailComponent

    var body: some View {
        let item = component.itemComponent(itemId: detail.itemId)

        VStack(alignment: .leading, spacing: 16) {
            Button("Back", action: component.back)
                .buttonStyle(.plain)

            Text("Stack detail")
                .font(.title2)
                .fontWeight(.semibold)

            SampleDetailContent(component: item)

            Button("Increment", action: item.increment)
                .buttonStyle(.borderedProminent)
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
    }
}

private struct SampleDetailContent: View {
    let component: SampleItemComponent
    @StateObject private var uiStateObserver: ObservableValue<SampleItemComponentUiState>

    init(component: SampleItemComponent) {
        self.component = component
        self._uiStateObserver = StateObject(
            wrappedValue: ObservableValue(component.uiState)
        )
    }

    var body: some View {
        let state = uiStateObserver.value

        Text(localizedFormat("%@ has independent count %lld.", state.title, Int64(state.count)))
        .font(.body)
        .foregroundStyle(.secondary)
    }
}

private struct ExamplesConfirmationOverlay: View {
    let component: ExamplesComponent

    var body: some View {
        ZStack {
            Color.black.opacity(0.25)
                .ignoresSafeArea()

            VStack(alignment: .leading, spacing: 16) {
                Text("Slot modal")
                    .font(.headline)

                Text("This dialog is hosted by a Child Slot and can be replaced with any optional child flow.")
                    .font(.body)
                    .foregroundStyle(.secondary)

                Button(
                    "Close",
                    action: component.dismissConfirmation
                )
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
            }
            .padding(20)
            .frame(maxWidth: 360)
            .background(.regularMaterial)
            .clipShape(RoundedRectangle(cornerRadius: 8))
            .padding(24)
        }
    }
}

private func title(for child: ExamplesComponentChild) -> String {
    switch child {
    case is ExamplesComponentChildList:
        return String(localized: "Navigation examples")
    case is ExamplesComponentChildDetail:
        return String(localized: "Stack detail")
    default:
        return ""
    }
}

private func localizedFormat(
    _ format: LocalizedStringResource,
    _ arguments: CVarArg...,
) -> String {
    String(
        format: String(localized: format),
        locale: Locale.current,
        arguments: arguments
    )
}

private func localizedPanelsModeName(_ modeName: String) -> String {
    switch modeName {
    case "SINGLE":
        return String(localized: "Single")
    case "DUAL":
        return String(localized: "Dual")
    case "TRIPLE":
        return String(localized: "Triple")
    default:
        return ""
    }
}

private func localizedBoolean(_ value: Bool) -> String {
    value ? String(localized: "Yes") : String(localized: "No")
}

#Preview("Examples - English") {
    ExamplesView(PreviewExamplesComponent())
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Examples - Spanish") {
    ExamplesView(PreviewExamplesComponent())
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Examples - Portuguese") {
    ExamplesView(PreviewExamplesComponent())
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Examples - English - Dark") {
    ExamplesView(PreviewExamplesComponent())
        .environment(\.locale, .init(identifier: "en"))
        .preferredColorScheme(.dark)
}

#Preview("Examples - Spanish - Dark") {
    ExamplesView(PreviewExamplesComponent())
        .environment(\.locale, .init(identifier: "es-419"))
        .preferredColorScheme(.dark)
}

#Preview("Examples - Portuguese - Dark") {
    ExamplesView(PreviewExamplesComponent())
        .environment(\.locale, .init(identifier: "pt-BR"))
        .preferredColorScheme(.dark)
}
