//
//  HomeView.swift
//  KMP-Template
//
//  Created by Adrian De León on 30/8/25.
//  Copyright © 2025 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct HomeView: View {
    private let component: HomeComponent
    @StateObject private var uiStateObserver: ObservableValue<HomeComponentUiState>

    init(_ component: HomeComponent) {
        self.component = component
        self._uiStateObserver = StateObject(
            wrappedValue: ObservableValue(component.uiState)
        )
    }

    var body: some View {
        if uiStateObserver.value.isReady {
            VStack(spacing: 12) {
                Text("Home")
                    .font(.title)
                    .fontWeight(.semibold)

                Text("This tab is the signed-in starting point for app-specific content.")
                    .font(.body)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 24)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .accessibilityIdentifier("home_screen")
        }
    }
}

#Preview("Home - English") {
    HomeView(PreviewHomeComponent())
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Home - Spanish") {
    HomeView(PreviewHomeComponent())
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Home - Portuguese") {
    HomeView(PreviewHomeComponent())
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Home - English - Dark") {
    HomeView(PreviewHomeComponent())
        .environment(\.locale, .init(identifier: "en"))
        .preferredColorScheme(.dark)
}
