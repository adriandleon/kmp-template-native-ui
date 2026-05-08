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
            HomeView(component.home)
                .tag(0)
                .tabItem { Label("Home", systemImage: "house") }

            ExamplesView(component.examples)
                .tag(1)
                .tabItem { Label("Examples", systemImage: "square.stack.3d.up") }

            PostsView(component.posts)
                .tag(2)
                .tabItem { Label("Posts", systemImage: "doc.text") }

            SettingsView(component.settings)
                .tag(3)
                .tabItem { Label("Settings", systemImage: "gearshape") }
        }
        .accessibilityIdentifier("main_screen")
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
