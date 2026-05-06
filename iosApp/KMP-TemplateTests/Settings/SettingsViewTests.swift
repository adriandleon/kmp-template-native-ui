//
//  SettingsViewTests.swift
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
@Suite("SettingsView Test Suite")
struct SettingsViewTests {
    private let sut = SettingsView(PreviewSettingsComponent())

    @Test("shows the settings screen from a preview component")
    func showsSettingsScreen() throws {
        let screen = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "settings_screen")

        #expect(!screen.isHidden())
    }
}
