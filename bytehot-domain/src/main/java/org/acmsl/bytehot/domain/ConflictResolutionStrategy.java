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
 * Filename: ConflictResolutionStrategy.java
 *
 * Author: Claude Code
 *
 * Class name: ConflictResolutionStrategy
 *
 * Responsibilities:
 *   - Define strategies for resolving conflicts during rollback operations
 *   - Provide enumeration of conflict resolution approaches
 *   - Support automated conflict resolution decision making
 *
 * Collaborators:
 *   - ConflictResolutionResult: Uses this enum to indicate strategy applied
 *   - RollbackManager: Uses this enum to resolve rollback conflicts
 */
package org.acmsl.bytehot.domain;

/**
 * Strategies for resolving conflicts during rollback operations
 * @author Claude Code
 * @since 2025-06-17
 */
public enum ConflictResolutionStrategy {

    /**
     * Merge concurrent changes with rollback state
     */
    MERGE_CHANGES("Merge concurrent changes with rollback state"),

    /**
     * Prioritize rollback state over concurrent changes
     */
    PREFER_ROLLBACK("Prioritize rollback state over concurrent changes"),

    /**
     * Prioritize current state over rollback state
     */
    PREFER_CURRENT("Prioritize current state over rollback state"),

    /**
     * Abort rollback when conflicts are detected
     */
    ABORT_ON_CONFLICT("Abort rollback when conflicts are detected"),

    /**
     * Force rollback ignoring all conflicts
     */
    FORCE_ROLLBACK("Force rollback ignoring all conflicts"),

    /**
     * Require manual intervention to resolve conflicts
     */
    MANUAL_RESOLUTION("Require manual intervention to resolve conflicts");

    /**
     * Human-readable description of the strategy
     */
    private final String description;

    /**
     * Creates a new conflict resolution strategy
     * @param description human-readable description
     */
    ConflictResolutionStrategy(final String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable description of this strategy
     * @return description of the strategy
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this strategy requires manual intervention
     * @return true if manual intervention is required
     */
    public boolean requiresManualIntervention() {
        return this == MANUAL_RESOLUTION;
    }

    /**
     * Returns whether this strategy is destructive (may lose data)
     * @return true if strategy may cause data loss
     */
    public boolean isDestructive() {
        return this == FORCE_ROLLBACK || this == PREFER_ROLLBACK;
    }

    /**
     * Returns whether this strategy is conservative (preserves existing state)
     * @return true if strategy preserves existing state
     */
    public boolean isConservative() {
        return this == ABORT_ON_CONFLICT || this == PREFER_CURRENT;
    }

    @Override
    public String toString() {
        return description;
    }
}