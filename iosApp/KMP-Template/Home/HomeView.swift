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

    init(_ component: HomeComponent) {
        self.component = component
    }

    var body: some View {
        Text(component.title)
            .font(.title)
    }
}
