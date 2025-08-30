//
//  RootView.swift
//  Template
//
//  Created by Adrian De León on 30/8/25.
//  Copyright © 2025 Template. All rights reserved.
//

import Shared
import SwiftUI

struct RootView: View {
    private let component: RootComponent

    init(_ component: RootComponent) {
        self.component = component
    }
    
    var body: some View {
        Text("Root View")
    }
}
