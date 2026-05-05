//
//  AuthView.swift
//  KMP-Template
//
//  Created by Adrian De León on 5/4/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct AuthView: View {
    private let component: AuthComponent

    @StateValue
    private var stack: ChildStack<AnyObject, AuthComponentChild>

    @StateValue
    private var modalSlot: ChildSlot<AnyObject, AuthComponentModalChild>

    init(_ component: AuthComponent) {
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
                AuthScreenContent(child: child, component: component)
            }
            .accessibilityIdentifier("auth_screen")

            if modalSlot.child?.instance is AuthComponentModalChildTerms {
                AuthTermsOverlay(component: component)
            }
        }
    }
}

private struct AuthScreenContent: View {
    let child: AuthComponentChild
    let component: AuthComponent

    var body: some View {
        let screen = screen(for: child)

        VStack(alignment: .leading, spacing: 24) {
            VStack(alignment: .leading, spacing: 12) {
                Text(title(for: child))
                    .font(.title)
                    .fontWeight(.semibold)
                    .accessibilityIdentifier("auth_title")

                Text(message(for: child))
                    .font(.body)
                    .foregroundStyle(.secondary)
                    .accessibilityIdentifier("auth_body")
            }

            Spacer()

            actions(for: screen)
        }
        .padding(24)
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .leading)
    }

    @ViewBuilder
    private func actions(for screen: AuthComponentScreen) -> some View {
        VStack(spacing: 12) {
            switch screen {
            case .signin:
                Button(NSLocalizedString("auth_sign_in_button", comment: ""), action: component.signIn)
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("auth_primary_button")

                HStack(spacing: 12) {
                    Button(NSLocalizedString("auth_create_account_button", comment: ""), action: component.openSignUp)
                        .buttonStyle(.bordered)
                        .frame(maxWidth: .infinity)
                        .accessibilityIdentifier("auth_secondary_button")

                    Button(NSLocalizedString("auth_forgot_button", comment: ""), action: component.openForgotPassword)
                        .buttonStyle(.bordered)
                        .frame(maxWidth: .infinity)
                        .accessibilityIdentifier("auth_forgot_button")
                }
            case .signup:
                Button(NSLocalizedString("auth_sign_up_button", comment: ""), action: component.signUp)
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("auth_primary_button")

                Button(NSLocalizedString("auth_terms_button", comment: ""), action: component.showTerms)
                    .buttonStyle(.plain)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("auth_terms_button")
            case .forgotpassword:
                Button(NSLocalizedString("auth_send_verification_button", comment: ""), action: component.requestVerification)
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("auth_primary_button")
            case .verification:
                Button(NSLocalizedString("auth_verification_done_button", comment: ""), action: component.back)
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("auth_primary_button")
            default:
                EmptyView()
            }
        }
    }
}

private struct AuthTermsOverlay: View {
    let component: AuthComponent

    var body: some View {
        ZStack {
            Color.black.opacity(0.25)
                .ignoresSafeArea()

            VStack(alignment: .leading, spacing: 16) {
                Text(NSLocalizedString("auth_terms_title", comment: ""))
                    .font(.headline)

                Text(NSLocalizedString("auth_terms_body", comment: ""))
                    .font(.body)
                    .foregroundStyle(.secondary)

                Button(NSLocalizedString("auth_terms_close_button", comment: ""), action: component.dismissModal)
                    .buttonStyle(.borderedProminent)
                    .frame(maxWidth: .infinity)
                    .accessibilityIdentifier("auth_terms_close_button")
            }
            .padding(20)
            .frame(maxWidth: 360)
            .background(.regularMaterial)
            .clipShape(RoundedRectangle(cornerRadius: 8))
            .padding(24)
        }
    }
}

private func screen(for child: AuthComponentChild) -> AuthComponentScreen {
    switch child {
    case let child as AuthComponentChildSignIn:
        return child.component.screen
    case let child as AuthComponentChildSignUp:
        return child.component.screen
    case let child as AuthComponentChildForgotPassword:
        return child.component.screen
    case let child as AuthComponentChildVerification:
        return child.component.screen
    default:
        return .signin
    }
}

private func title(for child: AuthComponentChild) -> String {
    switch screen(for: child) {
    case .signin:
        return NSLocalizedString("auth_sign_in_title", comment: "")
    case .signup:
        return NSLocalizedString("auth_sign_up_title", comment: "")
    case .forgotpassword:
        return NSLocalizedString("auth_forgot_title", comment: "")
    case .verification:
        return NSLocalizedString("auth_verification_title", comment: "")
    default:
        return ""
    }
}

private func message(for child: AuthComponentChild) -> String {
    switch screen(for: child) {
    case .signin:
        return NSLocalizedString("auth_sign_in_body", comment: "")
    case .signup:
        return NSLocalizedString("auth_sign_up_body", comment: "")
    case .forgotpassword:
        return NSLocalizedString("auth_forgot_body", comment: "")
    case .verification:
        return NSLocalizedString("auth_verification_body", comment: "")
    default:
        return ""
    }
}

#Preview("Auth - English") {
    AuthView(PreviewAuthComponent(initialScreen: .signin))
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Auth - Spanish") {
    AuthView(PreviewAuthComponent(initialScreen: .signin))
        .environment(\.locale, .init(identifier: "es-419"))
}

#Preview("Auth - Portuguese") {
    AuthView(PreviewAuthComponent(initialScreen: .signin))
        .environment(\.locale, .init(identifier: "pt-BR"))
}

#Preview("Auth - English - Dark") {
    AuthView(PreviewAuthComponent(initialScreen: .signin))
        .environment(\.locale, .init(identifier: "en"))
        .preferredColorScheme(.dark)
}

#Preview("Auth - Spanish - Dark") {
    AuthView(PreviewAuthComponent(initialScreen: .signin))
        .environment(\.locale, .init(identifier: "es-419"))
        .preferredColorScheme(.dark)
}

#Preview("Auth - Portuguese - Dark") {
    AuthView(PreviewAuthComponent(initialScreen: .signin))
        .environment(\.locale, .init(identifier: "pt-BR"))
        .preferredColorScheme(.dark)
}
