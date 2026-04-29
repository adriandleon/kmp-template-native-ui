//
//  PostsView.swift
//  KMP-Template
//
//  Created by Adrian De León on 4/29/26.
//  Copyright © 2026 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI

struct PostsView: View {
    private let component: PostsComponent
    @StateObject private var stateObserver: ObservableValue<PostsUiState>

    init(_ component: PostsComponent) {
        self.component = component
        self._stateObserver = StateObject(
            wrappedValue: ObservableValue(component.state)
        )
    }

    var body: some View {
        switch stateObserver.value {
        case is PostsUiState.Loading:
            PostsLoadingView()
        case let content as PostsUiState.Content:
            PostsListView(posts: content.posts)
        case let error as PostsUiState.Error:
            PostsErrorView(message: error.message, onRetry: component.onRetry)
        default:
            EmptyView()
        }
    }
}

private struct PostsLoadingView: View {
    var body: some View {
        ProgressView()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .accessibilityIdentifier("posts_loading")
    }
}

private struct PostsListView: View {
    let posts: [PostUiModel]

    var body: some View {
        List(posts, id: \.id) { post in
            VStack(alignment: .leading, spacing: 4) {
                Text(post.title)
                    .font(.headline)
                Text(post.body)
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
            }
            .accessibilityIdentifier("posts_item_\(post.id)")
        }
        .accessibilityIdentifier("posts_list")
        .navigationTitle(NSLocalizedString("posts_screen_title", comment: ""))
    }
}

private struct PostsErrorView: View {
    let message: String
    let onRetry: () -> Void

    var body: some View {
        VStack(spacing: 16) {
            Text(message)
                .multilineTextAlignment(.center)
            Button(NSLocalizedString("posts_retry_button", comment: ""), action: onRetry)
                .accessibilityIdentifier("posts_retry_button")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .accessibilityIdentifier("posts_error")
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private func postsLoadingPreview() -> some View {
    let component = PreviewPostsComponent()
    component.setState(newState: PostsUiState.Loading())
    return PostsView(component)
        .environment(\.locale, .init(identifier: "en"))
}

private func postsErrorPreview() -> some View {
    let component = PreviewPostsComponent()
    component.setState(newState: PostsUiState.Error(message: "Something went wrong."))
    return PostsView(component)
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Posts Loading – English") {
    postsLoadingPreview()
}

#Preview("Posts Content – English") {
    PostsView(PreviewPostsComponent())
        .environment(\.locale, .init(identifier: "en"))
}

#Preview("Posts Error – English") {
    postsErrorPreview()
}
