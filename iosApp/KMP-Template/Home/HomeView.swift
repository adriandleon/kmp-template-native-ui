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
        Text(uiStateObserver.value.title)
            .font(.title)
    }
}
