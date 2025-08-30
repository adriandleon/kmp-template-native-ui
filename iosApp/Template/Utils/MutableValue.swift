//
//  MutableValue.swift
//  Template
//
//  Created by Adrian De León on 30/8/25.
//  Copyright © 2025 Template. All rights reserved.
//

import Shared

func mutableValue<T: AnyObject>(_ initialValue: T) -> MutableValue<T> {
    // swiftlint:disable:next force_cast
    MutableValueBuilderKt.MutableValue(initialValue: initialValue) as! MutableValue<T>
}
