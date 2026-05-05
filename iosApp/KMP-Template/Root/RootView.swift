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
            case let child as RootComponentChildOnboarding:
                OnboardingView(child.component)
            case is RootComponentChildAuth:
                Text(NSLocalizedString("root_auth_placeholder", comment: ""))
            case is RootComponentChildMain:
                Text(NSLocalizedString("root_main_placeholder", comment: ""))
            default:
                Text(NSLocalizedString("root_starting_placeholder", comment: ""))
            }
        }
    }
}
