//
//  SettingsView.swift
//  KMP-Template
//
//  Created by Adrian De León on 5/6/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct SettingsView: View {
    private let component: SettingsComponent
    @StateObject private var uiStateObserver: ObservableValue<SettingsComponentUiState>

    init(_ component: SettingsComponent) {
        self.component = component
        self._uiStateObserver = StateObject(
            wrappedValue: ObservableValue(component.uiState)
        )
    }

    var body: some View {
        if uiStateObserver.value.isReady {
            VStack(spacing: 12) {
                Text("Settings")
                    .font(.title)
                    .fontWeight(.semibold)

                Text("This tab is the template place for account, preferences, and reset actions.")
                    .font(.body)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 24)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .accessibilityIdentifier("settings_screen")
        }
    }
}

#Preview("Settings - English") {
    SettingsView(PreviewSettingsComponent())
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Settings - Spanish") {
    SettingsView(PreviewSettingsComponent())
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Settings - Portuguese") {
    SettingsView(PreviewSettingsComponent())
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Settings - English - Dark") {
    SettingsView(PreviewSettingsComponent())
        .environment(\.locale, .init(identifier: "en"))
        .preferredColorScheme(.dark)
}
