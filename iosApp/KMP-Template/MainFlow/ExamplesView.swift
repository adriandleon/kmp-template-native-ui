//
//  ExamplesView.swift
//  KMP-Template
//
//  Created by OpenAI Codex on 5/5/26.
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
    @StateObject private var stateObserver: ObservableValue<ExamplesComponentState>

    init(component: ExamplesComponent) {
        self.component = component
        self._stateObserver = StateObject(
            wrappedValue: ObservableValue(component.state)
        )
    }

    var body: some View {
        let state = stateObserver.value

        ScrollView {
            VStack(alignment: .leading, spacing: 16) {
                Text(NSLocalizedString("examples_title", comment: ""))
                    .font(.title2)
                    .fontWeight(.semibold)

                Text(NSLocalizedString("examples_body", comment: ""))
                    .font(.body)
                    .foregroundStyle(.secondary)

                HStack(spacing: 12) {
                    Button(NSLocalizedString("examples_add_item_button", comment: ""), action: component.addItem)
                        .buttonStyle(.borderedProminent)
                        .accessibilityIdentifier("examples_add_button")

                    Button(NSLocalizedString("examples_show_modal_button", comment: ""), action: component.showConfirmation)
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

    @StateObject private var stateObserver: ObservableValue<SampleItemComponentState>

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
        self._stateObserver = StateObject(
            wrappedValue: ObservableValue(component.state)
        )
    }

    var body: some View {
        let state = stateObserver.value

        VStack(alignment: .leading, spacing: 8) {
            Text(
                String(
                    format: NSLocalizedString("examples_item_title_format", comment: ""),
                    state.title,
                    state.count
                )
            )
            .font(.headline)

            Text(
                NSLocalizedString(
                    isSelected ? "examples_item_selected" : "examples_item_not_selected",
                    comment: ""
                )
            )
            .font(.caption)
            .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button(NSLocalizedString("examples_select_item_button", comment: ""), action: onSelect)
                    .buttonStyle(.bordered)
                Button(NSLocalizedString("examples_increment_item_button", comment: ""), action: component.increment)
                    .buttonStyle(.bordered)
            }

            HStack(spacing: 8) {
                Button(NSLocalizedString("examples_open_detail_button", comment: ""), action: onOpenDetail)
                    .buttonStyle(.borderedProminent)
                Button(NSLocalizedString("examples_remove_item_button", comment: ""), action: onRemove)
                    .buttonStyle(.plain)
            }
        }
        .padding(.vertical, 10)
        .accessibilityIdentifier("examples_item")
    }
}

private struct PanelsShowcaseView: View {
    let component: ExamplesComponent
    let state: ExamplesComponentState

    private var selectedItemId: String? {
        state.selectedItemId ?? state.itemIds.first
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(NSLocalizedString("examples_panels_title", comment: ""))
                .font(.title3)
                .fontWeight(.semibold)

            Text(NSLocalizedString("examples_panels_body", comment: ""))
                .font(.body)
                .foregroundStyle(.secondary)

            Text(
                String(
                    format: NSLocalizedString("examples_panels_mode_format", comment: ""),
                    state.panelsMode.name
                )
            )
            .font(.caption)
            .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button(NSLocalizedString("examples_panels_single_mode", comment: "")) {
                    component.setPanelsMode(mode: .single)
                }
                .buttonStyle(.bordered)

                Button(NSLocalizedString("examples_panels_dual_mode", comment: "")) {
                    component.setPanelsMode(mode: .dual)
                }
                .buttonStyle(.bordered)

                Button(NSLocalizedString("examples_panels_triple_mode", comment: "")) {
                    component.setPanelsMode(mode: .triple)
                }
                .buttonStyle(.bordered)
            }

            Text(NSLocalizedString("examples_panels_main_title", comment: ""))
                .font(.headline)

            Text(NSLocalizedString("examples_panels_main_body", comment: ""))
                .font(.caption)
                .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button(NSLocalizedString("examples_panels_open_details_button", comment: "")) {
                    if let selectedItemId {
                        component.openPanelDetails(itemId: selectedItemId)
                    }
                }
                .buttonStyle(.borderedProminent)
                .disabled(selectedItemId == nil)

                Button(NSLocalizedString("examples_panels_open_extra_button", comment: "")) {
                    if let selectedItemId {
                        component.openPanelExtra(itemId: selectedItemId)
                    }
                }
                .buttonStyle(.bordered)
                .disabled(selectedItemId == nil)
            }

            Text(panelText(
                isVisible: state.hasPanelDetails,
                formatKey: "examples_panels_details_body_format",
                emptyKey: "examples_panels_details_empty"
            ))
            .font(.caption)
            .foregroundStyle(.secondary)

            if state.hasPanelDetails {
                Button(
                    NSLocalizedString("examples_panels_dismiss_details_button", comment: ""),
                    action: component.dismissPanelDetails
                )
                .buttonStyle(.plain)
            }

            Text(panelText(
                isVisible: state.hasPanelExtra,
                formatKey: "examples_panels_extra_body_format",
                emptyKey: "examples_panels_extra_empty"
            ))
            .font(.caption)
            .foregroundStyle(.secondary)

            if state.hasPanelExtra {
                Button(
                    NSLocalizedString("examples_panels_dismiss_extra_button", comment: ""),
                    action: component.dismissPanelExtra
                )
                .buttonStyle(.plain)
            }
        }
    }

    private func panelText(isVisible: Bool, formatKey: String, emptyKey: String) -> String {
        if isVisible {
            return String(
                format: NSLocalizedString(formatKey, comment: ""),
                state.panelItemId ?? ""
            )
        }

        return NSLocalizedString(emptyKey, comment: "")
    }
}

private struct GenericNavigationShowcaseView: View {
    let component: ExamplesComponent
    let state: ExamplesComponentState

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(NSLocalizedString("examples_generic_title", comment: ""))
                .font(.title3)
                .fontWeight(.semibold)

            Text(NSLocalizedString("examples_generic_body", comment: ""))
                .font(.body)
                .foregroundStyle(.secondary)

            Text(
                String(
                    format: NSLocalizedString("examples_generic_active_format", comment: ""),
                    state.activeWorkspacePaneId ?? ""
                )
            )
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
                Button(NSLocalizedString("examples_generic_add_button", comment: ""), action: component.addWorkspacePane)
                    .buttonStyle(.borderedProminent)

                Button(NSLocalizedString("examples_generic_close_button", comment: "")) {
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
    let state: ExamplesComponentState

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(NSLocalizedString("examples_deeplink_title", comment: ""))
                .font(.title3)
                .fontWeight(.semibold)

            Text(NSLocalizedString("examples_deeplink_body", comment: ""))
                .font(.body)
                .foregroundStyle(.secondary)

            HStack(spacing: 8) {
                Button(NSLocalizedString("examples_deeplink_item_button", comment: "")) {
                    _ = component.handleDeepLink(url: "template://examples/item/sample-3")
                }
                .buttonStyle(.borderedProminent)

                Button(NSLocalizedString("examples_deeplink_panel_button", comment: "")) {
                    _ = component.handleDeepLink(url: "template://examples/panel/sample-2")
                }
                .buttonStyle(.bordered)
            }

            HStack(spacing: 8) {
                Button(NSLocalizedString("examples_deeplink_workspace_button", comment: "")) {
                    _ = component.handleDeepLink(url: "template://examples/workspace/pane-2")
                }
                .buttonStyle(.bordered)

                Button(NSLocalizedString("examples_deeplink_modal_button", comment: "")) {
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
            return NSLocalizedString("examples_deeplink_empty", comment: "")
        }

        return String(
            format: NSLocalizedString("examples_deeplink_status_format", comment: ""),
            lastDeepLinkPath,
            String(state.lastDeepLinkHandled?.boolValue ?? false)
        )
    }
}

private struct ExamplesDetailView: View {
    let component: ExamplesComponent
    let detail: ExamplesComponentDetailComponent

    var body: some View {
        let item = component.itemComponent(itemId: detail.itemId)

        VStack(alignment: .leading, spacing: 16) {
            Button(NSLocalizedString("examples_back_button", comment: ""), action: component.back)
                .buttonStyle(.plain)

            Text(NSLocalizedString("examples_detail_title", comment: ""))
                .font(.title2)
                .fontWeight(.semibold)

            SampleDetailContent(component: item)

            Button(NSLocalizedString("examples_increment_item_button", comment: ""), action: item.increment)
                .buttonStyle(.borderedProminent)
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
    }
}

private struct SampleDetailContent: View {
    let component: SampleItemComponent
    @StateObject private var stateObserver: ObservableValue<SampleItemComponentState>

    init(component: SampleItemComponent) {
        self.component = component
        self._stateObserver = StateObject(
            wrappedValue: ObservableValue(component.state)
        )
    }

    var body: some View {
        let state = stateObserver.value

        Text(
            String(
                format: NSLocalizedString("examples_detail_body_format", comment: ""),
                state.title,
                state.count
            )
        )
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
                Text(NSLocalizedString("examples_confirmation_title", comment: ""))
                    .font(.headline)

                Text(NSLocalizedString("examples_confirmation_body", comment: ""))
                    .font(.body)
                    .foregroundStyle(.secondary)

                Button(
                    NSLocalizedString("examples_confirmation_close_button", comment: ""),
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
        return NSLocalizedString("examples_title", comment: "")
    case is ExamplesComponentChildDetail:
        return NSLocalizedString("examples_detail_title", comment: "")
    default:
        return ""
    }
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
