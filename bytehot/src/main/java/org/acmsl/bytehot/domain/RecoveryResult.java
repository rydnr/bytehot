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
 * Filename: RecoveryResult.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryResult
 *
 * Responsibilities:
 *   - Encapsulate the results of error recovery operations
 *   - Provide success/failure status and detailed recovery information
 *   - Track recovery action, timing, and context
 *
 * Collaborators:
 *   - RecoveryAction: Enum defining types of recovery actions performed
 *   - ErrorRecoveryManager: Uses this class to report recovery results
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Results of error recovery operations
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class RecoveryResult {

    /**
     * Unique identifier for this recovery operation
     */
    private final String recoveryId;

    /**
     * Whether the recovery was successful
     */
    private final boolean successful;

    /**
     * The recovery action that was performed
     */
    private final RecoveryAction action;

    /**
     * Descriptive message about the recovery result
     */
    private final String message;

    /**
     * The class name that was being recovered
     */
    private final String className;

    /**
     * When the recovery operation completed
     */
    private final Instant timestamp;

    /**
     * Whether this recovery requires immediate action
     */
    private final boolean requiresImmediateAction;

    /**
     * Creates a new recovery result
     * @param successful whether the recovery was successful
     * @param action the recovery action performed
     * @param message descriptive message
     * @param className the class name
     * @param timestamp when the recovery completed
     * @param requiresImmediateAction whether immediate action is required
     */
    private RecoveryResult(final boolean successful, final RecoveryAction action, final String message,
                          final String className, final Instant timestamp, final boolean requiresImmediateAction) {
        this.recoveryId = UUID.randomUUID().toString();
        this.successful = successful;
        this.action = action;
        this.message = message;
        this.className = className;
        this.timestamp = timestamp;
        this.requiresImmediateAction = requiresImmediateAction;
    }

    /**
     * Creates a successful recovery result
     * @param action the recovery action performed
     * @param message descriptive message
     * @param className the class name
     * @param timestamp when the recovery completed
     * @return successful recovery result
     */
    public static RecoveryResult success(final RecoveryAction action, final String message,
                                        final String className, final Instant timestamp) {
        return new RecoveryResult(true, action, message, className, timestamp, false);
    }

    /**
     * Creates a failed recovery result
     * @param action the recovery action that was attempted
     * @param message descriptive error message
     * @param className the class name
     * @param timestamp when the recovery failed
     * @return failed recovery result
     */
    public static RecoveryResult failure(final RecoveryAction action, final String message,
                                        final String className, final Instant timestamp) {
        return new RecoveryResult(false, action, message, className, timestamp, false);
    }

    /**
     * Creates an emergency shutdown recovery result
     * @param message descriptive message
     * @param className the class name
     * @param timestamp when the shutdown was initiated
     * @return emergency shutdown recovery result
     */
    public static RecoveryResult emergencyShutdown(final String message, final String className, final Instant timestamp) {
        return new RecoveryResult(true, RecoveryAction.EMERGENCY_SHUTDOWN, message, className, timestamp, true);
    }

    /**
     * Returns whether this recovery operation was successful
     * @return true if successful
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Returns whether this recovery requires immediate action
     * @return true if immediate action is required
     */
    public boolean requiresImmediateAction() {
        return requiresImmediateAction;
    }

    @Override
    public String toString() {
        return "RecoveryResult{" +
               "id='" + recoveryId + '\'' +
               ", successful=" + successful +
               ", action=" + action +
               ", message='" + message + '\'' +
               ", className='" + className + '\'' +
               ", timestamp=" + timestamp +
               ", requiresImmediateAction=" + requiresImmediateAction +
               '}';
    }
}