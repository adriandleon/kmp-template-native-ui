//
//  RootView.swift
//  KMP-Template
//
//  Created by Adrian De León on 30/8/25.
//  Copyright © 2025 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct RootView: View {
    private let component: RootComponent

    init(_ component: RootComponent) {
        self.component = component
    }

    var body: some View {
        RootSlotView(component)
    }
}

private struct RootSlotView: View {
    @StateValue
    private var slotValue: ChildSlot<AnyObject, RootComponentChild>

    init(_ component: RootComponent) {
        _slotValue = StateValue(component.slot)
    }

    var body: some View {
        VStack {
            switch slotValue.child?.instance {
            case is RootComponentChildStartup:
                StartupView()
            case let child as RootComponentChildOnboarding:
                OnboardingView(child.component)
            case let child as RootComponentChildAuth:
                AuthView(child.component)
            case let child as RootComponentChildMain:
                MainView(child.component)
            default:
                StartupView()
            }
        }
    }
}

private struct StartupView: View {
    var body: some View {
        VStack(spacing: 16) {
            ProgressView()
            Text("Starting")
                .font(.body)
                .foregroundStyle(.secondary)
        }
    }
}

#Preview("Root startup - English") {
    StartupView()
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Root startup - Spanish") {
    StartupView()
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Root startup - Portuguese") {
    StartupView()
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Root startup - English - Dark") {
    StartupView()
        .environment(\.locale, .init(identifier: "en"))
        .preferredColorScheme(.dark)
}
