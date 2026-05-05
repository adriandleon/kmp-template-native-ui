//
//  OnboardingView.swift
//  KMP-Template
//
//  Created by OpenAI Codex on 5/4/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct OnboardingView: View {
    private let component: OnboardingComponent
    @StateObject private var uiStateObserver: ObservableValue<OnboardingComponentUiState>

    init(_ component: OnboardingComponent) {
        self.component = component
        self._uiStateObserver = StateObject(
            wrappedValue: ObservableValue(component.uiState)
        )
    }

    var body: some View {
        let state = uiStateObserver.value

        VStack(spacing: 24) {
            HStack {
                Spacer()
                Button(NSLocalizedString("onboarding_skip_button", comment: ""), action: component.skip)
                    .accessibilityIdentifier("onboarding_skip_button")
            }

            Spacer()

            VStack(spacing: 16) {
                Text(title(for: state.selectedPage))
                    .font(.title)
                    .fontWeight(.semibold)
                    .multilineTextAlignment(.center)
                    .accessibilityIdentifier("onboarding_title")

                Text(message(for: state.selectedPage))
                    .font(.body)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                    .accessibilityIdentifier("onboarding_body")
            }
            .frame(maxWidth: .infinity)

            Spacer()

            HStack(spacing: 8) {
                ForEach(0..<Int(state.pageCount), id: \.self) { index in
                    Circle()
                        .fill(index == Int(state.selectedIndex) ? Color.accentColor : Color.secondary.opacity(0.3))
                        .frame(width: 8, height: 8)
                }
            }
            .accessibilityIdentifier("onboarding_page_indicator")

            HStack(spacing: 12) {
                Button(NSLocalizedString("onboarding_previous_button", comment: ""), action: component.previous)
                    .buttonStyle(.bordered)
                    .disabled(!state.canGoPrevious)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("onboarding_previous_button")

                Button(
                    NSLocalizedString(
                        state.isLastPage ? "onboarding_finish_button" : "onboarding_next_button",
                        comment: ""
                    ),
                    action: component.finish
                )
                .buttonStyle(.borderedProminent)
                .frame(maxWidth: .infinity)
                .accessibilityIdentifier("onboarding_next_button")
            }
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .accessibilityIdentifier("onboarding_screen")
    }
}

private func title(for page: OnboardingComponentPage) -> String {
    switch page {
    case .welcome:
        return NSLocalizedString("onboarding_welcome_title", comment: "")
    case .organize:
        return NSLocalizedString("onboarding_organize_title", comment: "")
    case .customize:
        return NSLocalizedString("onboarding_customize_title", comment: "")
    default:
        return ""
    }
}

private func message(for page: OnboardingComponentPage) -> String {
    switch page {
    case .welcome:
        return NSLocalizedString("onboarding_welcome_body", comment: "")
    case .organize:
        return NSLocalizedString("onboarding_organize_body", comment: "")
    case .customize:
        return NSLocalizedString("onboarding_customize_body", comment: "")
    default:
        return ""
    }
}

#Preview("Onboarding - English") {
    OnboardingView(PreviewOnboardingComponent(initialPage: .welcome))
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Onboarding - Spanish") {
    OnboardingView(PreviewOnboardingComponent(initialPage: .welcome))
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Onboarding - Portuguese") {
    OnboardingView(PreviewOnboardingComponent(initialPage: .welcome))
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Onboarding - English - Dark") {
    OnboardingView(PreviewOnboardingComponent(initialPage: .welcome))
        .environment(\.locale, .init(identifier: "en"))
        .preferredColorScheme(.dark)
}

#Preview("Onboarding - Spanish - Dark") {
    OnboardingView(PreviewOnboardingComponent(initialPage: .welcome))
        .environment(\.locale, .init(identifier: "es-419"))
        .preferredColorScheme(.dark)
}

#Preview("Onboarding - Portuguese - Dark") {
    OnboardingView(PreviewOnboardingComponent(initialPage: .welcome))
        .environment(\.locale, .init(identifier: "pt-BR"))
        .preferredColorScheme(.dark)
}
