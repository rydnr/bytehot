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
 * Filename: RollbackOperation.java
 *
 * Author: Claude Code
 *
 * Class name: RollbackOperation
 *
 * Responsibilities:
 *   - Define the types of rollback operations that can be performed
 *   - Provide enumeration of all possible rollback operation types
 *   - Support rollback operation classification and reporting
 *
 * Collaborators:
 *   - RollbackResult: Uses this enum to indicate the operation performed
 *   - RollbackManager: Uses this enum to classify rollback operations
 */
package org.acmsl.bytehot.domain;

/**
 * Types of rollback operations that can be performed
 * @author Claude Code
 * @since 2025-06-17
 */
public enum RollbackOperation {

    /**
     * Complete restoration to a previous snapshot state
     */
    FULL_RESTORE("Full restoration to previous state"),

    /**
     * Restore only instance state without affecting bytecode
     */
    INSTANCE_STATE_RESTORE("Instance state restoration"),

    /**
     * Restore bytecode to previous version
     */
    BYTECODE_RESTORE("Bytecode restoration"),

    /**
     * Restore class metadata and structure
     */
    METADATA_RESTORE("Class metadata restoration"),

    /**
     * Partial rollback of specific changes
     */
    PARTIAL_RESTORE("Partial state restoration"),

    /**
     * Cascading rollback across multiple related classes
     */
    CASCADING_RESTORE("Cascading restoration across related classes");

    /**
     * Human-readable description of the rollback operation
     */
    private final String description;

    /**
     * Creates a new rollback operation with description
     * @param description human-readable description
     */
    RollbackOperation(final String description) {
        this.description = description;
    }

    /**
     * Gets the human-readable description of this rollback operation
     * @return description of the operation
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether this operation affects instance state
     * @return true if instance state is affected
     */
    public boolean affectsInstanceState() {
        return this == FULL_RESTORE || this == INSTANCE_STATE_RESTORE || this == PARTIAL_RESTORE;
    }

    /**
     * Returns whether this operation affects bytecode
     * @return true if bytecode is affected
     */
    public boolean affectsBytecode() {
        return this == FULL_RESTORE || this == BYTECODE_RESTORE || this == PARTIAL_RESTORE;
    }

    /**
     * Returns whether this operation is complex (involves multiple components)
     * @return true if operation is complex
     */
    public boolean isComplex() {
        return this == FULL_RESTORE || this == CASCADING_RESTORE || this == PARTIAL_RESTORE;
    }

    @Override
    public String toString() {
        return description;
    }
}