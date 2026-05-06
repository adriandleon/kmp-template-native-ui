//
//  MainView.swift
//  KMP-Template
//
//  Created by Adrian De León on 5/4/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct MainView: View {
    private let component: MainComponent
    @StateObject private var uiStateObserver: ObservableValue<MainComponentUiState>

    init(_ component: MainComponent) {
        self.component = component
        self._uiStateObserver = StateObject(
            wrappedValue: ObservableValue(component.uiState)
        )
    }

    var body: some View {
        TabView(
            selection: Binding(
                get: { Int(uiStateObserver.value.selectedIndex) },
                set: { component.selectPage(index: Int32($0)) },
            )
        ) {
            MainPageView(page: .home)
                .tag(0)
                .tabItem { Label("Home", systemImage: "house") }

            ExamplesView(component.examples)
                .tag(1)
                .tabItem { Label("Examples", systemImage: "square.stack.3d.up") }

            MainPageView(page: .adaptive)
                .tag(2)
                .tabItem { Label("Adaptive", systemImage: "sidebar.left") }

            MainPageView(page: .settings)
                .tag(3)
                .tabItem { Label("Settings", systemImage: "gearshape") }
        }
        .accessibilityIdentifier("main_screen")
    }
}

private struct MainPageView: View {
    let page: MainComponentPage

    var body: some View {
        VStack(spacing: 12) {
            Text(title(for: page))
                .font(.title)
                .fontWeight(.semibold)

            Text(message(for: page))
                .font(.body)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .padding(.horizontal, 24)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .accessibilityIdentifier("main_page_content")
    }
}

private func title(for page: MainComponentPage) -> String {
    switch page {
    case .home:
        return String(localized: "Home")
    case .examples:
        return String(localized: "Examples")
    case .adaptive:
        return String(localized: "Adaptive")
    case .settings:
        return String(localized: "Settings")
    default:
        return ""
    }
}

private func message(for page: MainComponentPage) -> String {
    switch page {
    case .home:
        return String(localized: "This tab is the signed-in starting point for app-specific content.")
    case .examples:
        return String(localized: "This tab will host stack, slot, and child item navigation examples.")
    case .adaptive:
        return String(localized: "This tab will host the Child Panels adaptive layout example.")
    case .settings:
        return String(localized: "This tab is the template place for account, preferences, and reset actions.")
    default:
        return ""
    }
}

#Preview("Main - English") {
    MainView(PreviewMainComponent(initialPage: .home))
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Main - Spanish") {
    MainView(PreviewMainComponent(initialPage: .home))
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Main - Portuguese") {
    MainView(PreviewMainComponent(initialPage: .home))
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Main - English - Dark") {
    MainView(PreviewMainComponent(initialPage: .home))
        .environment(\.locale, .init(identifier: "en"))
        .preferredColorScheme(.dark)
}

#Preview("Main - Spanish - Dark") {
    MainView(PreviewMainComponent(initialPage: .home))
        .environment(\.locale, .init(identifier: "es-419"))
        .preferredColorScheme(.dark)
}

#Preview("Main - Portuguese - Dark") {
    MainView(PreviewMainComponent(initialPage: .home))
        .environment(\.locale, .init(identifier: "pt-BR"))
        .preferredColorScheme(.dark)
}
