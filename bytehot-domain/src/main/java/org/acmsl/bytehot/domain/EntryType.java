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
 * Filename: EntryType.java
 *
 * Author: Claude Code
 *
 * Enum name: EntryType
 *
 * Responsibilities:
 *   - Define types of entries in the rollback audit trail
 *   - Support categorization of audit trail operations
 */
package org.acmsl.bytehot.domain;

/**
 * Audit entry type enumeration for rollback audit trail operations.
 * Used to categorize different types of audit entries in the rollback system.
 * @author Claude Code
 * @since 2025-06-25
 */
public enum EntryType {
    /**
     * Snapshot Created - A new snapshot was created for a class.
     * This entry type indicates that a backup snapshot was successfully
     * created for a class before performing any modifications.
     */
    SNAPSHOT_CREATED,
    
    /**
     * Bytecode Snapshot Created - A bytecode-level snapshot was created.
     * This entry type indicates that a detailed bytecode snapshot was
     * created for precise rollback capabilities at the bytecode level.
     */
    BYTECODE_SNAPSHOT_CREATED,
    
    /**
     * Rollback Performed - A rollback operation was successfully executed.
     * This entry type indicates that a rollback operation completed
     * successfully and the system was restored to a previous state.
     */
    ROLLBACK_PERFORMED,
    
    /**
     * Rollback Failed - A rollback operation failed to complete.
     * This entry type indicates that a rollback operation was attempted
     * but failed due to errors or inconsistencies in the system state.
     */
    ROLLBACK_FAILED,
    
    /**
     * Cleanup Performed - Cleanup operations were executed on old snapshots.
     * This entry type indicates that maintenance operations were performed
     * to clean up old or unnecessary snapshots to manage system resources.
     */
    CLEANUP_PERFORMED
}