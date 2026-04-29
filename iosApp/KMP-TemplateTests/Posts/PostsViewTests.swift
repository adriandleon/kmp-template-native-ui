//
//  PostsViewTests.swift
//  KMP-TemplateTests
//
//  Created by Adrian De León on 4/29/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

@testable import KMP_Template
import Shared
import Testing
import ViewInspector

@MainActor
@Suite("PostsView Test Suite")
struct PostsViewTests {
    private let component = PreviewPostsComponent()
    private let sut: PostsView

    init() {
        sut = PostsView(component)
    }

    // ── State visibility ─────────────────────────────────────────────────────

    @Test("shows loading view when state is Loading")
    func showsLoadingViewWhenLoading() async throws {
        component.setState(newState: PostsUiState.Loading())

        let loadingView = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_loading")

        #expect(!loadingView.isHidden())
    }

    @Test("shows posts list when state is Content")
    func showsPostsListWhenContent() async throws {
        // Default state is Content with previewPosts
        let listView = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_list")

        #expect(!listView.isHidden())
    }

    @Test("shows correct number of posts in Content state")
    func showsCorrectPostCount() async throws {
        let posts = PreviewPostsComponent.companion.previewPosts
        let expectedCount = posts.count

        let listView = try sut.inspect()
            .find(ViewType.List.self)

        #expect(try listView.count == expectedCount)
    }

    @Test("shows error view when state is Error")
    func showsErrorViewWhenError() async throws {
        component.setState(newState: PostsUiState.Error(message: "Oops"))

        let errorView = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_error")

        #expect(!errorView.isHidden())
    }

    // ── Interactions ─────────────────────────────────────────────────────────

    @Test("calls onRetry when retry button is tapped")
    func callsOnRetryWhenRetryTapped() async throws {
        component.setState(newState: PostsUiState.Error(message: "Oops"))

        let retryButton = try sut.inspect()
            .find(viewWithAccessibilityIdentifier: "posts_retry_button")
            .button()

        try retryButton.tap()

        #expect(component.retryCallCount == 1)
    }
}
