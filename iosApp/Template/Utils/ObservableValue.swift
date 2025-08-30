//
//  ObservableValue.swift
//  Template
//
//  Created by Adrian De León on 30/8/25.
//  Copyright © 2025 Misión Vida. All rights reserved.
//

import Shared
import SwiftUI

public class ObservableValue<T: AnyObject>: ObservableObject {
    @Published
    var value: T

    private var cancellation: Cancellation?

    init(_ value: Value<T>) {
        self.value = value.value
        cancellation = value.subscribe { [weak self] value in self?.value = value }
    }

    deinit {
        cancellation?.cancel()
    }
}
