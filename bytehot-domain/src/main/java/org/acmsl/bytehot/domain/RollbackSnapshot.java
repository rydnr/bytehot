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
 * Filename: RollbackSnapshot.java
 *
 * Author: Claude Code
 *
 * Class name: RollbackSnapshot
 *
 * Responsibilities:
 *   - Capture state information for potential rollback operations
 *   - Store class state, instance count, and bytecode information
 *   - Provide immutable snapshot data for rollback manager
 *
 * Collaborators:
 *   - RollbackManager: Uses snapshots for rollback operations
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

/**
 * Captures state information for potential rollback operations
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class RollbackSnapshot {

    /**
     * Unique identifier for this snapshot
     */
    private final String snapshotId;

    /**
     * The class name this snapshot captures
     */
    private final String className;

    /**
     * Number of instances at snapshot time
     */
    private final int instanceCount;

    /**
     * Bytecode captured at snapshot time (optional)
     */
    private final byte[] bytecode;

    /**
     * When this snapshot was created
     */
    private final Instant timestamp;

    /**
     * Creates a new rollback snapshot
     * @param className the class name
     * @param instanceCount number of instances
     * @param bytecode optional bytecode
     * @param timestamp when snapshot was created
     */
    private RollbackSnapshot(final String className, final int instanceCount, final byte[] bytecode, final Instant timestamp) {
        this.snapshotId = UUID.randomUUID().toString();
        this.className = className;
        this.instanceCount = instanceCount;
        this.bytecode = bytecode != null ? Arrays.copyOf(bytecode, bytecode.length) : null;
        this.timestamp = timestamp;
    }

    /**
     * Creates a snapshot without bytecode
     * @param className the class name
     * @param instanceCount number of instances
     * @param timestamp when snapshot was created
     * @return rollback snapshot
     */
    public static RollbackSnapshot create(final String className, final int instanceCount, final Instant timestamp) {
        return new RollbackSnapshot(className, instanceCount, null, timestamp);
    }

    /**
     * Creates a snapshot with bytecode
     * @param className the class name
     * @param instanceCount number of instances
     * @param bytecode the bytecode to capture
     * @param timestamp when snapshot was created
     * @return rollback snapshot with bytecode
     */
    public static RollbackSnapshot createWithBytecode(final String className, final int instanceCount, 
                                                     final byte[] bytecode, final Instant timestamp) {
        return new RollbackSnapshot(className, instanceCount, bytecode, timestamp);
    }

    /**
     * Returns whether this snapshot includes bytecode
     * @return true if bytecode is captured
     */
    public boolean hasBytecode() {
        return bytecode != null;
    }

    /**
     * Gets a copy of the captured bytecode
     * @return copy of bytecode or null if not captured
     */
    public byte[] getBytecode() {
        return bytecode != null ? Arrays.copyOf(bytecode, bytecode.length) : null;
    }

    @Override
    public String toString() {
        return "RollbackSnapshot{" +
               "id='" + snapshotId + '\'' +
               ", className='" + className + '\'' +
               ", instanceCount=" + instanceCount +
               ", hasBytecode=" + hasBytecode() +
               ", timestamp=" + timestamp +
               '}';
    }
}