//
//  ExamplesViewTests.swift
//  KMP-TemplateTests
//
//  Created by Adrian De León on 5/5/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

@testable import KMP_Template
import Shared
import Testing
import ViewInspector

@MainActor
@Suite("ExamplesView Test Suite")
struct ExamplesViewTests {
    private let component = PreviewExamplesComponent()
    private let sut: ExamplesView

    init() {
        sut = ExamplesView(component)
    }

    @Test("shows the examples screen from a preview component")
    func showsExamplesScreen() throws {
        let screen = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "examples_screen")

        #expect(!screen.isHidden())
    }

    @Test("updates preview component state when add is tapped")
    func updatesPreviewComponentStateWhenAddingItem() throws {
        let addButton = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "examples_add_button")
            .button()

        try addButton.tap()

        #expect(component.uiState.value.selectedItemId == "sample-4")
    }

    @Test("shows confirmation overlay when modal action is tapped")
    func showsConfirmationOverlay() throws {
        let modalButton = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "examples_modal_button")
            .button()

        try modalButton.tap()

        let confirmationTitle = try sut.inspect()
            .find(text: "Slot modal")

        #expect(!confirmationTitle.isHidden())
    }
}
