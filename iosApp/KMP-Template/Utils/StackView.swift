//
//  StackView.swift
//  KMP-Template
//
//  Created by Adrian De León on 30/8/25.
//  Copyright © 2025 KMP-Template. All rights reserved.
//

import Shared
import SwiftUI
import UIKit

// swiftlint:disable force_unwrapping
struct StackView<T: AnyObject, Content: View>: View {
    @StateValue
    var stackValue: ChildStack<AnyObject, T>

    var getTitle: (T) -> String
    var onBack: (_ toIndex: Int32) -> Void

    @ViewBuilder
    var childContent: (T) -> Content

    private var stack: [Child<AnyObject, T>] { stackValue.items }

    var body: some View {
        /*
         iOS 16.0 has an issue with swipe back see
         https://stackoverflow.com/questions/73978107/incomplete-swipe-back-gesture-causes-navigationpath-mismanagement
         */
        if #available(iOS 16.1, *) {
            NavigationStack(
                path: Binding(
                    get: { stack.dropFirst() },
                    set: { updatedPath in onBack(Int32(updatedPath.count)) },
                ),
            ) {
                childContent(stack.first!.instance!)
                    .navigationDestination(for: Child<AnyObject, T>.self) {
                        childContent($0.instance!)
                    }
            }
        } else {
            StackInteropView(
                components: stack.map { $0.instance! },
                getTitle: getTitle,
                onBack: onBack,
                childContent: childContent,
            )
        }
    }
}

private struct StackInteropView<T: AnyObject, Content: View>: UIViewControllerRepresentable {
    var components: [T]
    var getTitle: (T) -> String
    var onBack: (_ toIndex: Int32) -> Void
    var childContent: (T) -> Content

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: Context) -> UINavigationController {
        context.coordinator.syncChanges(self)
        let navigationController = UINavigationController(
            rootViewController: context.coordinator.viewControllers.first!)

        return navigationController
    }

    func updateUIViewController(_ navigationController: UINavigationController, context: Context) {
        context.coordinator.syncChanges(self)
        navigationController.setViewControllers(context.coordinator.viewControllers, animated: true)
    }

    private func createViewController(_ component: T, _ coordinator: Coordinator) -> NavigationItemHostingController {
        let controller = NavigationItemHostingController(rootView: childContent(component))
        controller.coordinator = coordinator
        controller.component = component
        controller.onBack = onBack
        controller.navigationItem.title = getTitle(component)
        return controller
    }

    class Coordinator: NSObject {
        var parent: StackInteropView<T, Content>
        var viewControllers = [NavigationItemHostingController]()
        var preservedComponents = [T]()

        init(_ parent: StackInteropView<T, Content>) {
            self.parent = parent
        }

        func syncChanges(_ parent: StackInteropView<T, Content>) {
            self.parent = parent
            let count = max(preservedComponents.count, parent.components.count)

            for index in 0 ..< count {
                if index >= parent.components.count {
                    viewControllers.removeLast()
                } else if index >= preservedComponents.count {
                    viewControllers.append(parent.createViewController(parent.components[index], self))
                } else if parent.components[index] !== preservedComponents[index] {
                    viewControllers[index] = parent.createViewController(parent.components[index], self)
                }
            }

            preservedComponents = parent.components
        }
    }

    class NavigationItemHostingController: UIHostingController<Content> {
        fileprivate(set) weak var coordinator: Coordinator?
        fileprivate(set) var component: T?
        fileprivate(set) var onBack: ((_ toIndex: Int32) -> Void)?

        override func viewDidAppear(_ animated: Bool) {
            super.viewDidAppear(animated)

            guard let components = coordinator?.preservedComponents else { return }
            guard let index = components.firstIndex(where: { $0 === component }) else { return }

            if index < components.count - 1 {
                onBack?(Int32(index))
            }
        }
    }
}

// swiftlint:enable force_unwrapping
