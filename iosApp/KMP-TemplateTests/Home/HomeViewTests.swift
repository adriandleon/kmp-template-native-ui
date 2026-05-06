//
//  HomeViewTests.swift
//  KMP-TemplateTests
//
//  Created by Adrian De León on 5/6/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

@testable import KMP_Template
import Shared
import Testing
import ViewInspector

@MainActor
@Suite("HomeView Test Suite")
struct HomeViewTests {
    private let sut = HomeView(PreviewHomeComponent())

    @Test("shows the home screen from a preview component")
    func showsHomeScreen() throws {
        let screen = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "home_screen")

        #expect(!screen.isHidden())
    }
}
