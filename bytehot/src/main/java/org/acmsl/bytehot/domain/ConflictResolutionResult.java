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
 * Filename: ConflictResolutionResult.java
 *
 * Author: Claude Code
 *
 * Class name: ConflictResolutionResult
 *
 * Responsibilities:
 *   - Encapsulate results of conflict resolution during rollback operations
 *   - Track whether conflicts were detected and how they were resolved
 *   - Provide detailed information about conflict resolution strategies
 *
 * Collaborators:
 *   - ConflictResolutionStrategy: Strategy used for resolving conflicts
 *   - RollbackManager: Uses this class for conflict resolution results
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Results of conflict resolution during rollback operations
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class ConflictResolutionResult {

    /**
     * Whether conflicts were detected
     */
    private final boolean hasConflicts;

    /**
     * Whether the resolution was successful
     */
    private final boolean successful;

    /**
     * The resolution strategy that was applied
     */
    private final ConflictResolutionStrategy resolutionStrategy;

    /**
     * Descriptive message about the conflict resolution
     */
    private final String message;

    /**
     * The class name where conflicts occurred
     */
    private final String className;

    /**
     * When the conflict resolution completed
     */
    private final Instant timestamp;

    /**
     * Creates a new conflict resolution result
     * @param hasConflicts whether conflicts were detected
     * @param successful whether resolution was successful
     * @param resolutionStrategy the strategy used
     * @param message descriptive message
     * @param className the class name
     * @param timestamp when resolution completed
     */
    private ConflictResolutionResult(final boolean hasConflicts, final boolean successful,
                                   final ConflictResolutionStrategy resolutionStrategy, final String message,
                                   final String className, final Instant timestamp) {
        this.hasConflicts = hasConflicts;
        this.successful = successful;
        this.resolutionStrategy = resolutionStrategy;
        this.message = message;
        this.className = className;
        this.timestamp = timestamp;
    }

    /**
     * Creates a result for conflicts that were detected and resolved
     * @param strategy the resolution strategy used
     * @param message descriptive message
     * @param className the class name
     * @param timestamp when resolution completed
     * @return conflict resolution result with conflicts
     */
    public static ConflictResolutionResult withConflicts(final ConflictResolutionStrategy strategy, final String message,
                                                        final String className, final Instant timestamp) {
        return new ConflictResolutionResult(true, true, strategy, message, className, timestamp);
    }

    /**
     * Creates a result for cases where no conflicts were detected
     * @param message descriptive message
     * @param className the class name
     * @param timestamp when operation completed
     * @return conflict resolution result without conflicts
     */
    public static ConflictResolutionResult withoutConflicts(final String message, final String className, final Instant timestamp) {
        return new ConflictResolutionResult(false, true, null, message, className, timestamp);
    }

    /**
     * Creates a result for failed conflict resolution
     * @param message descriptive error message
     * @param className the class name
     * @param timestamp when failure occurred
     * @return failed conflict resolution result
     */
    public static ConflictResolutionResult failure(final String message, final String className, final Instant timestamp) {
        return new ConflictResolutionResult(true, false, null, message, className, timestamp);
    }

    /**
     * Returns whether conflicts were detected
     * @return true if conflicts were found
     */
    public boolean hasConflicts() {
        return hasConflicts;
    }

    /**
     * Returns whether the resolution was successful
     * @return true if successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    @Override
    public String toString() {
        return "ConflictResolutionResult{" +
               "hasConflicts=" + hasConflicts +
               ", successful=" + successful +
               ", resolutionStrategy=" + resolutionStrategy +
               ", message='" + message + '\'' +
               ", className='" + className + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}