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
 * Filename: RecoveryAction.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryAction
 *
 * Responsibilities:
 *   - Define the types of recovery actions that can be performed
 *   - Provide enumeration of all possible recovery operations
 *   - Support error recovery decision making
 *
 * Collaborators:
 *   - RecoveryResult: Uses this enum to indicate the action performed
 *   - ErrorRecoveryManager: Uses this enum to determine recovery actions
 */
package org.acmsl.bytehot.domain;

/**
 * Types of recovery actions that can be performed during error recovery
 * @author Claude Code
 * @since 2025-06-17
 */
public enum RecoveryAction {

    /**
     * Roll back changes to the previous stable state
     */
    ROLLBACK_CHANGES("Rollback changes to previous stable state"),

    /**
     * Preserve the current state without making changes
     */
    PRESERVE_CURRENT_STATE("Preserve current state without changes"),

    /**
     * Reject the proposed change due to validation errors
     */
    REJECT_CHANGE("Reject proposed change due to errors"),

    /**
     * Retry the failed operation with exponential backoff
     */
    RETRY_OPERATION("Retry failed operation"),

    /**
     * Initiate emergency shutdown to prevent further damage
     */
    EMERGENCY_SHUTDOWN("Initiate emergency shutdown"),

    /**
     * Activate fallback mode with reduced functionality
     */
    FALLBACK_MODE("Activate fallback mode"),

    /**
     * Require manual intervention to resolve the issue
     */
    MANUAL_INTERVENTION("Require manual intervention"),

    /**
     * No recovery action is required
     */
    NO_ACTION("No recovery action required");

    /**
     * Human-readable description of the recovery action
     */
    private final String description;

    /**
     * Creates a new recovery action with description
     * @param description human-readable description
     */
    RecoveryAction(final String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable description of this recovery action
     * @return description of the action
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this recovery action requires immediate attention
     * @return true if immediate attention is required
     */
    public boolean requiresImmediateAttention() {
        return this == EMERGENCY_SHUTDOWN || this == MANUAL_INTERVENTION;
    }

    /**
     * Returns whether this recovery action modifies system state
     * @return true if system state is modified
     */
    public boolean modifiesState() {
        return this == ROLLBACK_CHANGES || this == FALLBACK_MODE || this == EMERGENCY_SHUTDOWN;
    }

    @Override
    public String toString() {
        return description;
    }
}