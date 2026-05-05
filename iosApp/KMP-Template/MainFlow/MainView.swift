//
//  MainView.swift
//  KMP-Template
//
//  Created by OpenAI Codex on 5/4/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct MainView: View {
    private let component: MainComponent
    @StateObject private var stateObserver: ObservableValue<MainComponentState>

    init(_ component: MainComponent) {
        self.component = component
        self._stateObserver = StateObject(
            wrappedValue: ObservableValue(component.state)
        )
    }

    var body: some View {
        TabView(
            selection: Binding(
                get: { Int(stateObserver.value.selectedIndex) },
                set: { component.selectPage(index: Int32($0)) },
            )
        ) {
            MainPageView(page: .home)
                .tag(0)
                .tabItem { Label(NSLocalizedString("main_home_tab", comment: ""), systemImage: "house") }

            MainPageView(page: .examples)
                .tag(1)
                .tabItem { Label(NSLocalizedString("main_examples_tab", comment: ""), systemImage: "square.stack.3d.up") }

            MainPageView(page: .adaptive)
                .tag(2)
                .tabItem { Label(NSLocalizedString("main_adaptive_tab", comment: ""), systemImage: "sidebar.left") }

            MainPageView(page: .settings)
                .tag(3)
                .tabItem { Label(NSLocalizedString("main_settings_tab", comment: ""), systemImage: "gearshape") }
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
        return NSLocalizedString("main_home_title", comment: "")
    case .examples:
        return NSLocalizedString("main_examples_title", comment: "")
    case .adaptive:
        return NSLocalizedString("main_adaptive_title", comment: "")
    case .settings:
        return NSLocalizedString("main_settings_title", comment: "")
    default:
        return ""
    }
}

private func message(for page: MainComponentPage) -> String {
    switch page {
    case .home:
        return NSLocalizedString("main_home_body", comment: "")
    case .examples:
        return NSLocalizedString("main_examples_body", comment: "")
    case .adaptive:
        return NSLocalizedString("main_adaptive_body", comment: "")
    case .settings:
        return NSLocalizedString("main_settings_body", comment: "")
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
