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
 * Filename: RollbackResult.java
 *
 * Author: Claude Code
 *
 * Class name: RollbackResult
 *
 * Responsibilities:
 *   - Encapsulate the results of rollback operations
 *   - Provide success/failure status and detailed rollback information
 *   - Track rollback operation type, timing, and context
 *
 * Collaborators:
 *   - RollbackOperation: Enum defining types of rollback operations
 *   - RollbackManager: Uses this class to report rollback results
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Results of rollback operations
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class RollbackResult {

    /**
     * Whether the rollback was successful
     */
    private final boolean successful;

    /**
     * Whether the rollback timed out
     */
    private final boolean timedOut;

    /**
     * The rollback operation that was performed
     */
    private final RollbackOperation operation;

    /**
     * Descriptive message about the rollback result
     */
    private final String message;

    /**
     * The class name that was rolled back
     */
    private final String className;

    /**
     * The snapshot ID used for rollback
     */
    private final String snapshotId;

    /**
     * When the rollback operation completed
     */
    private final Instant timestamp;

    /**
     * Creates a new rollback result
     * @param successful whether the rollback was successful
     * @param timedOut whether the rollback timed out
     * @param operation the rollback operation performed
     * @param message descriptive message
     * @param className the class name
     * @param snapshotId the snapshot ID
     * @param timestamp when the rollback completed
     */
    private RollbackResult(final boolean successful, final boolean timedOut, final RollbackOperation operation,
                          final String message, final String className, final String snapshotId, final Instant timestamp) {
        this.successful = successful;
        this.timedOut = timedOut;
        this.operation = operation;
        this.message = message;
        this.className = className;
        this.snapshotId = snapshotId;
        this.timestamp = timestamp;
    }

    /**
     * Creates a successful rollback result
     * @param operation the rollback operation performed
     * @param message descriptive message
     * @param className the class name
     * @param snapshotId the snapshot ID
     * @param timestamp when the rollback completed
     * @return successful rollback result
     */
    public static RollbackResult success(final RollbackOperation operation, final String message,
                                        final String className, final String snapshotId, final Instant timestamp) {
        return new RollbackResult(true, false, operation, message, className, snapshotId, timestamp);
    }

    /**
     * Creates a failed rollback result
     * @param operation the rollback operation that was attempted
     * @param message descriptive error message
     * @param className the class name
     * @param snapshotId the snapshot ID
     * @param timestamp when the rollback failed
     * @return failed rollback result
     */
    public static RollbackResult failure(final RollbackOperation operation, final String message,
                                        final String className, final String snapshotId, final Instant timestamp) {
        return new RollbackResult(false, false, operation, message, className, snapshotId, timestamp);
    }

    /**
     * Creates a timeout rollback result
     * @param operation the rollback operation that timed out
     * @param message descriptive timeout message
     * @param className the class name
     * @param snapshotId the snapshot ID
     * @param timestamp when the timeout occurred
     * @return timeout rollback result
     */
    public static RollbackResult timeout(final RollbackOperation operation, final String message,
                                        final String className, final String snapshotId, final Instant timestamp) {
        return new RollbackResult(false, true, operation, message, className, snapshotId, timestamp);
    }

    /**
     * Returns whether this rollback operation was successful
     * @return true if successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Returns whether this rollback operation timed out
     * @return true if timed out
     */
    public boolean isTimedOut() {
        return timedOut;
    }

    @Override
    public String toString() {
        return "RollbackResult{" +
               "successful=" + successful +
               ", timedOut=" + timedOut +
               ", operation=" + operation +
               ", message='" + message + '\'' +
               ", className='" + className + '\'' +
               ", snapshotId='" + snapshotId + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
}