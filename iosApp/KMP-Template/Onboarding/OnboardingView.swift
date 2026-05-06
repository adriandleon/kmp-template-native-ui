//
//  OnboardingView.swift
//  KMP-Template
//
//  Created by Adrian De León on 5/4/26.
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
                Button("Skip", action: component.skip)
                    .accessibilityIdentifier("onboarding_skip_button")
            }

            TabView(
                selection: Binding(
                    get: { Int(uiStateObserver.value.selectedIndex) },
                    set: { component.selectPage(index: Int32($0)) },
                )
            ) {
                ForEach(0..<Int(state.pageCount), id: \.self) { index in
                    OnboardingPageView(page: page(for: index))
                        .tag(index)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            .animation(.easeInOut(duration: 0.3), value: Int(state.selectedIndex))
            .frame(maxWidth: .infinity, maxHeight: .infinity)

            HStack(spacing: 8) {
                ForEach(0..<Int(state.pageCount), id: \.self) { index in
                    Circle()
                        .fill(index == Int(state.selectedIndex) ? Color.accentColor : Color.secondary.opacity(0.3))
                        .frame(width: 8, height: 8)
                }
            }
            .accessibilityIdentifier("onboarding_page_indicator")

            HStack(spacing: 12) {
                Button("Previous") {
                    withAnimation(.easeInOut(duration: 0.3)) {
                        component.previous()
                    }
                }
                    .buttonStyle(.bordered)
                    .disabled(!state.canGoPrevious)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("onboarding_previous_button")

                Button {
                    withAnimation(.easeInOut(duration: 0.3)) {
                        component.finish()
                    }
                } label: {
                    Text(state.isLastPage ? "Finish" : "Next")
                }
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

private struct OnboardingPageView: View {
    let page: OnboardingComponentPage

    var body: some View {
        VStack(spacing: 16) {
            Text(title(for: page))
                .font(.title)
                .fontWeight(.semibold)
                .multilineTextAlignment(.center)
                .accessibilityIdentifier("onboarding_title")

            Text(message(for: page))
                .font(.body)
                .foregroundStyle(.secondary)
                .multilineTextAlignment(.center)
                .accessibilityIdentifier("onboarding_body")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(.horizontal, 16)
    }
}

private func page(for index: Int) -> OnboardingComponentPage {
    switch index {
    case 0:
        return .welcome
    case 1:
        return .organize
    case 2:
        return .customize
    default:
        return .welcome
    }
}

private func title(for page: OnboardingComponentPage) -> LocalizedStringResource {
    switch page {
    case .welcome:
        return "Welcome"
    case .organize:
        return "Organize"
    case .customize:
        return "Customize"
    default:
        return ""
    }
}

private func message(for page: OnboardingComponentPage) -> LocalizedStringResource {
    switch page {
    case .welcome:
        return "Start with a shared flow that decides what the app shows first."
    case .organize:
        return "Keep page order and completion rules in shared Kotlin components."
    case .customize:
        return "Replace the copy and screens while preserving the Child Pages pattern."
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
