//
//  MainApplication.swift
//  Template
//
//  Created by Adrian De León on 30/8/25.
//  Copyright © 2025 Template. All rights reserved.
//

import Shared
import SwiftUI

@main
struct MainApplication: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    private var appDelegate: AppDelegate

    init() {
        FirebaseHelperKt.startCrashKiOS()
        KoinAppKt.doInitKoin()
    }

    var body: some Scene {
        WindowGroup {
            RootView(appDelegate.root)
        }
    }
}

private class AppDelegate: NSObject, UIApplicationDelegate {
    let root: RootComponent = DefaultRootComponent(
        componentContext: DefaultComponentContext(lifecycle: ApplicationLifecycle()),
    )
}
