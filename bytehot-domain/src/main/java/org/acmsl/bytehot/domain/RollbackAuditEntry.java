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
 * Filename: RollbackAuditEntry.java
 *
 * Author: Claude Code
 *
 * Class name: RollbackAuditEntry
 *
 * Responsibilities:
 *   - Represent individual entries in the rollback audit trail
 *   - Capture detailed information about specific rollback operations
 *   - Support audit analysis and compliance reporting
 *
 * Collaborators:
 *   - RollbackAuditTrail: Contains multiple audit entries
 *   - RollbackManager: Creates audit entries for operations
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Individual entry in the rollback audit trail
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class RollbackAuditEntry {


    /**
     * Type of this audit entry
     */
    private final EntryType entryType;

    /**
     * The snapshot ID associated with this entry
     */
    private final String snapshotId;

    /**
     * The class name associated with this entry
     */
    private final String className;

    /**
     * The rollback operation performed (if applicable)
     */
    private final RollbackOperation operation;

    /**
     * When this entry was created
     */
    private final Instant timestamp;

    /**
     * Additional details about the entry
     */
    private final String details;

    /**
     * Creates a new rollback audit entry
     * @param entryType type of entry
     * @param snapshotId snapshot ID
     * @param className class name
     * @param operation rollback operation (optional)
     * @param timestamp when entry was created
     * @param details additional details
     */
    private RollbackAuditEntry(final EntryType entryType, final String snapshotId, final String className,
                              final RollbackOperation operation, final Instant timestamp, final String details) {
        this.entryType = entryType;
        this.snapshotId = snapshotId;
        this.className = className;
        this.operation = operation;
        this.timestamp = timestamp;
        this.details = details;
    }

    /**
     * Creates an audit entry for snapshot creation
     * @param snapshotId the snapshot ID
     * @param className the class name
     * @param timestamp when snapshot was created
     * @return audit entry
     */
    public static RollbackAuditEntry snapshotCreated(final String snapshotId, final String className, final Instant timestamp) {
        return new RollbackAuditEntry(EntryType.SNAPSHOT_CREATED, snapshotId, className, null, timestamp,
                                     "Snapshot created for class " + className);
    }

    /**
     * Creates an audit entry for bytecode snapshot creation
     * @param snapshotId the snapshot ID
     * @param className the class name
     * @param timestamp when snapshot was created
     * @return audit entry
     */
    public static RollbackAuditEntry bytecodeSnapshotCreated(final String snapshotId, final String className, final Instant timestamp) {
        return new RollbackAuditEntry(EntryType.BYTECODE_SNAPSHOT_CREATED, snapshotId, className, null, timestamp,
                                     "Bytecode snapshot created for class " + className);
    }

    /**
     * Creates an audit entry for rollback operation
     * @param snapshotId the snapshot ID
     * @param className the class name
     * @param operation the rollback operation
     * @param timestamp when rollback was performed
     * @return audit entry
     */
    public static RollbackAuditEntry rollbackPerformed(final String snapshotId, final String className,
                                                       final RollbackOperation operation, final Instant timestamp) {
        return new RollbackAuditEntry(EntryType.ROLLBACK_PERFORMED, snapshotId, className, operation, timestamp,
                                     "Rollback performed: " + operation.getDescription());
    }

    /**
     * Creates an audit entry for failed rollback
     * @param snapshotId the snapshot ID
     * @param className the class name
     * @param operation the attempted operation
     * @param timestamp when failure occurred
     * @param errorMessage error details
     * @return audit entry
     */
    public static RollbackAuditEntry rollbackFailed(final String snapshotId, final String className,
                                                    final RollbackOperation operation, final Instant timestamp,
                                                    final String errorMessage) {
        return new RollbackAuditEntry(EntryType.ROLLBACK_FAILED, snapshotId, className, operation, timestamp,
                                     "Rollback failed: " + errorMessage);
    }

    /**
     * Creates an audit entry for cleanup operation
     * @param cleanedCount number of snapshots cleaned
     * @param timestamp when cleanup was performed
     * @return audit entry
     */
    public static RollbackAuditEntry cleanupPerformed(final int cleanedCount, final Instant timestamp) {
        return new RollbackAuditEntry(EntryType.CLEANUP_PERFORMED, null, null, null, timestamp,
                                     "Cleanup performed: " + cleanedCount + " snapshots removed");
    }

    @Override
    public String toString() {
        return "RollbackAuditEntry{" +
               "type=" + entryType +
               ", snapshotId='" + snapshotId + '\'' +
               ", className='" + className + '\'' +
               ", operation=" + operation +
               ", timestamp=" + timestamp +
               ", details='" + details + '\'' +
               '}';
    }
}