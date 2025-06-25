/*
                        ByteHot

    Copyright (C) 2025-today  rydnr@acm-sl.org

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General Public License for more details.

    You should have received a copy of the GNU General Public v3
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    Thanks to ACM S.L. for distributing this library under the GPLv3 license.
    Contact info: jose.sanleandro@acm-sl.com

 ******************************************************************************
 *
 * Filename: TestFailureSeverity.java
 *
 * Author: Claude Code
 *
 * Enum name: TestFailureSeverity
 *
 * Responsibilities:
 *   - Define severity levels for test failures in event-driven testing
 *   - Support classification and prioritization of test issues
 */
package org.acmsl.bytehot.testing.support;

import lombok.Getter;

/**
 * Test failure severity enumeration for event-driven testing scenarios.
 * Used to classify and prioritize test failures based on their impact.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum TestFailureSeverity {
    /**
     * Blocking Severity - Test infrastructure failure preventing test execution.
     * This severity indicates that the testing infrastructure itself has failed,
     * preventing proper test execution and requiring immediate attention.
     */
    BLOCKING("Blocking - Test infrastructure failure"),
    
    /**
     * Critical Severity - Core functionality is completely broken.
     * This severity indicates that essential system functionality is broken,
     * making the system unusable and requiring urgent fixes.
     */
    CRITICAL("Critical - Core functionality broken"),
    
    /**
     * Major Severity - Important feature is broken but system remains usable.
     * This severity indicates that an important feature doesn't work properly
     * but the system can still function for most use cases.
     */
    MAJOR("Major - Important feature broken"),
    
    /**
     * Minor Severity - Edge case or non-critical feature failure.
     * This severity indicates that a minor feature or edge case scenario
     * fails but doesn't impact the main functionality significantly.
     */
    MINOR("Minor - Edge case or non-critical feature"),
    
    /**
     * Flaky Severity - Intermittent or timing-related test failure.
     * This severity indicates that the test fails inconsistently,
     * often due to timing issues, race conditions, or test environment instability.
     */
    FLAKY("Flaky - Intermittent or timing-related failure");

    /**
     * Human-readable description of the severity level
     */
    @Getter
    private final String description;

    /**
     * Creates a test failure severity with the specified description.
     * @param description the human-readable description
     */
    TestFailureSeverity(final String description) {
        this.description = description;
    }
}