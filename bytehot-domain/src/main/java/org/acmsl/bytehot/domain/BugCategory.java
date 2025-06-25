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
 * Filename: BugCategory.java
 *
 * Author: Claude Code
 *
 * Enum name: BugCategory
 *
 * Responsibilities:
 *   - Define categories for bug classification in bug reports
 *   - Group related types of issues for analysis and tracking
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

/**
 * Bug category classifications for organizing and analyzing different types of issues.
 * Used by bug report generation systems to group related problems and identify patterns.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum BugCategory {
    /**
     * Memory Management - Leaks or excessive usage.
     * Issues related to memory allocation, deallocation, or excessive memory consumption
     * that can lead to OutOfMemoryError or performance degradation.
     */
    MEMORY_LEAK("Memory Management - Leaks or excessive usage"),
    
    /**
     * Concurrency - Race conditions or deadlocks.
     * Issues related to multi-threaded access, synchronization problems,
     * race conditions, or deadlocks that affect system stability.
     */
    CONCURRENT_ACCESS("Concurrency - Race conditions or deadlocks"),
    
    /**
     * Validation - Input or state validation failure.
     * Issues related to improper validation of input parameters, system state,
     * or business rule enforcement that leads to unexpected behavior.
     */
    VALIDATION_ERROR("Validation - Input or state validation failure"),
    
    /**
     * Configuration - Setup or config issues.
     * Issues related to system configuration, property files, environment setup,
     * or initialization parameters that prevent proper operation.
     */
    CONFIGURATION_ERROR("Configuration - Setup or config issues"),
    
    /**
     * Dependencies - Missing or incompatible dependencies.
     * Issues related to missing libraries, incompatible versions,
     * or dependency injection failures that prevent system functionality.
     */
    DEPENDENCY_ERROR("Dependencies - Missing or incompatible dependencies"),
    
    /**
     * Performance - Slow execution or timeouts.
     * Issues related to poor performance, slow response times,
     * timeouts, or inefficient algorithms that impact user experience.
     */
    PERFORMANCE_ISSUE("Performance - Slow execution or timeouts"),
    
    /**
     * Security - Potential security issues.
     * Issues related to security vulnerabilities, improper access controls,
     * or potential attack vectors that compromise system security.
     */
    SECURITY_VULNERABILITY("Security - Potential security issues"),
    
    /**
     * Data Integrity - Corruption or inconsistency.
     * Issues related to data corruption, inconsistent state,
     * or integrity violations that affect data reliability.
     */
    DATA_CORRUPTION("Data Integrity - Corruption or inconsistency"),
    
    /**
     * Network - Connectivity or communication issues.
     * Issues related to network communication, connectivity failures,
     * protocol errors, or distributed system coordination problems.
     */
    NETWORK_ERROR("Network - Connectivity or communication issues"),
    
    /**
     * Unknown - Unable to categorize automatically.
     * Issues that don't fit into other categories or require
     * manual analysis to determine the appropriate classification.
     */
    UNKNOWN("Unknown - Unable to categorize automatically");

    /**
     * Human-readable description of the bug category
     */
    @Getter
    private final String description;

    /**
     * Creates a bug category with the specified description.
     * @param description the human-readable description of this bug category
     */
    BugCategory(final String description) {
        this.description = description;
    }
}