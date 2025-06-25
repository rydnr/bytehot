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
 * Filename: BugSeverity.java
 *
 * Author: Claude Code
 *
 * Enum name: BugSeverity
 *
 * Responsibilities:
 *   - Define severity levels for bug classification in bug reports
 *   - Provide descriptive information for each severity level
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

/**
 * Bug severity levels for classifying the impact and urgency of reported issues.
 * Used by bug report generation and error analysis systems to prioritize fixes.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum BugSeverity {
    /**
     * Critical severity - System failure or data loss.
     * These issues cause complete system failure, data corruption, or security breaches.
     * Requires immediate attention and should block releases.
     */
    CRITICAL("Critical - System failure or data loss"),
    
    /**
     * High severity - Major functionality affected.
     * These issues significantly impact core functionality but don't cause system failure.
     * Should be fixed in the current release cycle.
     */
    HIGH("High - Major functionality affected"),
    
    /**
     * Medium severity - Partial functionality affected.
     * These issues affect some functionality but workarounds exist.
     * Should be prioritized for upcoming releases.
     */
    MEDIUM("Medium - Partial functionality affected"),
    
    /**
     * Low severity - Minor issue or cosmetic.
     * These issues have minimal impact on functionality or are cosmetic in nature.
     * Can be addressed in future releases.
     */
    LOW("Low - Minor issue or cosmetic"),
    
    /**
     * Informational - Not a bug but notable behavior.
     * These are observations about system behavior that may need attention
     * but don't represent actual defects.
     */
    INFO("Informational - Not a bug but notable behavior");

    /**
     * Human-readable description of the severity level
     */
    @Getter
    private final String description;

    /**
     * Creates a bug severity with the specified description.
     * @param description the human-readable description of this severity level
     */
    BugSeverity(final String description) {
        this.description = description;
    }
}