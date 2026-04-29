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
        StackView(
            stackValue: StateValue(component.stack),
            getTitle: {
                switch $0 {
                case is RootComponentChildHome: ""
                case is RootComponentChildPosts: ""
                default: ""
                }
            },
            onBack: { _ in },
            childContent: {
                switch $0 {
                case let child as RootComponentChildHome: HomeView(child.component)
                case let posts as RootComponentChildPosts: PostsView(posts.component)
                default: EmptyView()
                }
            },
        )
    }
}
