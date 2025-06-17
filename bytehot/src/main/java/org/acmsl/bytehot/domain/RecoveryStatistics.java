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
 * Filename: RecoveryStatistics.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryStatistics
 *
 * Responsibilities:
 *   - Track metrics and statistics for error recovery operations
 *   - Provide recovery success rates and operational metrics
 *   - Support monitoring and alerting for recovery system health
 *
 * Collaborators:
 *   - ErrorRecoveryManager: Generates and provides these statistics
 *   - RecoveryResult: Individual recovery operations tracked in statistics
 */
package org.acmsl.bytehot.domain;

import lombok.Getter;

import java.time.Instant;

/**
 * Statistics and metrics for error recovery operations
 * @author Claude Code
 * @since 2025-06-17
 */
@Getter
public class RecoveryStatistics {

    /**
     * Total number of recovery operations attempted
     */
    private final long totalRecoveryOperations;

    /**
     * Number of successful recovery operations
     */
    private final long successfulRecoveries;

    /**
     * Success rate as a percentage (0.0 to 1.0)
     */
    private final double recoverySuccessRate;

    /**
     * Timestamp of the last recovery operation
     */
    private final Instant lastRecoveryTime;

    /**
     * Creates new recovery statistics
     * @param totalRecoveryOperations total operations attempted
     * @param successfulRecoveries number of successful operations
     * @param recoverySuccessRate success rate (0.0 to 1.0)
     * @param lastRecoveryTime timestamp of last recovery
     */
    private RecoveryStatistics(final long totalRecoveryOperations, final long successfulRecoveries,
                              final double recoverySuccessRate, final Instant lastRecoveryTime) {
        this.totalRecoveryOperations = totalRecoveryOperations;
        this.successfulRecoveries = successfulRecoveries;
        this.recoverySuccessRate = recoverySuccessRate;
        this.lastRecoveryTime = lastRecoveryTime;
    }

    /**
     * Creates recovery statistics
     * @param totalOperations total recovery operations
     * @param successfulOperations successful recovery operations
     * @param successRate success rate (0.0 to 1.0)
     * @param lastRecoveryTime last recovery timestamp
     * @return recovery statistics instance
     */
    public static RecoveryStatistics create(final long totalOperations, final long successfulOperations,
                                           final double successRate, final Instant lastRecoveryTime) {
        return new RecoveryStatistics(totalOperations, successfulOperations, successRate, lastRecoveryTime);
    }

    /**
     * Gets the number of failed recovery operations
     * @return number of failed operations
     */
    public long getFailedRecoveries() {
        return totalRecoveryOperations - successfulRecoveries;
    }

    /**
     * Gets the failure rate as a percentage (0.0 to 1.0)
     * @return failure rate
     */
    public double getRecoveryFailureRate() {
        return 1.0 - recoverySuccessRate;
    }

    /**
     * Returns whether the recovery system is healthy based on success rate
     * @return true if success rate is above threshold (80%)
     */
    public boolean isRecoverySystemHealthy() {
        return recoverySuccessRate >= 0.8;
    }

    /**
     * Returns whether there have been any recovery operations
     * @return true if at least one recovery operation has been performed
     */
    public boolean hasRecoveryOperations() {
        return totalRecoveryOperations > 0;
    }

    @Override
    public String toString() {
        return "RecoveryStatistics{" +
               "totalOperations=" + totalRecoveryOperations +
               ", successfulRecoveries=" + successfulRecoveries +
               ", successRate=" + String.format("%.2f%%", recoverySuccessRate * 100) +
               ", lastRecoveryTime=" + lastRecoveryTime +
               ", systemHealthy=" + isRecoverySystemHealthy() +
               '}';
    }
}