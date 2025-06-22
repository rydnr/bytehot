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
 * Filename: RollbackAuditTrail.java
 *
 * Author: Claude Code
 *
 * Class name: RollbackAuditTrail
 *
 * Responsibilities:
 *   - Maintain comprehensive audit trail of rollback operations
 *   - Track operation statistics and performance metrics
 *   - Provide detailed history of rollback activities
 *
 * Collaborators:
 *   - RollbackAuditEntry: Individual audit entries in the trail
 *   - RollbackManager: Creates and maintains the audit trail
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Comprehensive audit trail of rollback operations
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class RollbackAuditTrail {

    /**
     * Total number of rollback operations performed
     */
    private final long totalOperations;

    /**
     * Number of successful rollback operations
     */
    private final long successfulOperations;

    /**
     * Timestamp of the last rollback operation
     */
    private final Instant lastOperationTime;

    /**
     * Detailed history of rollback operations
     */
    private final List<RollbackAuditEntry> operationHistory;

    /**
     * Creates a new rollback audit trail
     * @param totalOperations total operations performed
     * @param successfulOperations successful operations
     * @param lastOperationTime last operation timestamp
     * @param operationHistory detailed operation history
     */
    private RollbackAuditTrail(final long totalOperations, final long successfulOperations,
                              final Instant lastOperationTime, final List<RollbackAuditEntry> operationHistory) {
        this.totalOperations = totalOperations;
        this.successfulOperations = successfulOperations;
        this.lastOperationTime = lastOperationTime;
        this.operationHistory = operationHistory;
    }

    /**
     * Creates a rollback audit trail
     * @param totalOperations total operations performed
     * @param successfulOperations successful operations
     * @param lastOperationTime last operation timestamp
     * @param operationHistory detailed operation history
     * @return rollback audit trail
     */
    public static RollbackAuditTrail create(final long totalOperations, final long successfulOperations,
                                           final Instant lastOperationTime, final List<RollbackAuditEntry> operationHistory) {
        return new RollbackAuditTrail(totalOperations, successfulOperations, lastOperationTime, operationHistory);
    }

    /**
     * Gets the number of failed operations
     * @return failed operation count
     */
    public long getFailedOperations() {
        return totalOperations - successfulOperations;
    }

    /**
     * Gets the success rate as a percentage (0.0 to 1.0)
     * @return success rate
     */
    public double getSuccessRate() {
        return totalOperations > 0 ? (double) successfulOperations / totalOperations : 0.0;
    }

    /**
     * Returns whether any operations have been performed
     * @return true if operations have been performed
     */
    public boolean hasOperations() {
        return totalOperations > 0;
    }

    @Override
    public String toString() {
        return "RollbackAuditTrail{" +
               "totalOperations=" + totalOperations +
               ", successfulOperations=" + successfulOperations +
               ", successRate=" + String.format("%.2f%%", getSuccessRate() * 100) +
               ", lastOperationTime=" + lastOperationTime +
               ", historyEntries=" + operationHistory.size() +
               '}';
    }
}