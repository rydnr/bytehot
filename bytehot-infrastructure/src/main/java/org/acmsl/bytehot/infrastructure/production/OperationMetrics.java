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
 * Filename: OperationMetrics.java
 *
 * Author: Claude Code
 *
 * Class name: OperationMetrics
 *
 * Responsibilities:
 *   - Track metrics for specific operation types
 *   - Calculate success rates and performance statistics
 *   - Support operation performance analysis
 *
 * Collaborators:
 *   - PerformanceMonitor: Uses metrics for operation tracking
 */
package org.acmsl.bytehot.infrastructure.production;

/**
 * Tracks metrics for specific hot-swap operation types.
 * @author Claude Code
 * @since 2025-07-04
 */
public class OperationMetrics {
    
    /**
     * The operation type name.
     */
    private final String operationType;
    
    /**
     * Total number of operations of this type.
     */
    private final long totalOperations;
    
    /**
     * Total time spent on operations of this type.
     */
    private final long totalTime;
    
    /**
     * Number of successful operations.
     */
    private final long successfulOperations;
    
    /**
     * Creates a new OperationMetrics.
     * @param operationType The operation type
     * @param totalOperations Total operations count
     * @param totalTime Total time in milliseconds
     * @param successfulOperations Successful operations count
     */
    public OperationMetrics(final String operationType,
                          final long totalOperations,
                          final long totalTime,
                          final long successfulOperations) {
        this.operationType = operationType;
        this.totalOperations = totalOperations;
        this.totalTime = totalTime;
        this.successfulOperations = successfulOperations;
    }
    
    /**
     * Creates updated metrics with a new operation.
     * @param operationTime The operation time in milliseconds
     * @param successful Whether the operation was successful
     * @return Updated operation metrics
     */
    public OperationMetrics withOperation(final long operationTime, final boolean successful) {
        return new OperationMetrics(
            operationType,
            totalOperations + 1,
            totalTime + operationTime,
            successfulOperations + (successful ? 1 : 0)
        );
    }
    
    /**
     * Gets the operation type.
     * @return The operation type
     */
    public String getOperationType() {
        return operationType;
    }
    
    /**
     * Gets the total operations count.
     * @return The total operations
     */
    public long getTotalOperations() {
        return totalOperations;
    }
    
    /**
     * Gets the total time.
     * @return The total time in milliseconds
     */
    public long getTotalTime() {
        return totalTime;
    }
    
    /**
     * Gets the successful operations count.
     * @return The successful operations
     */
    public long getSuccessfulOperations() {
        return successfulOperations;
    }
    
    /**
     * Gets the failed operations count.
     * @return The failed operations
     */
    public long getFailedOperations() {
        return totalOperations - successfulOperations;
    }
    
    /**
     * Calculates the success rate.
     * @return The success rate (0.0 to 1.0)
     */
    public double getSuccessRate() {
        if (totalOperations == 0) {
            return 0.0;
        }
        return (double) successfulOperations / totalOperations;
    }
    
    /**
     * Calculates the failure rate.
     * @return The failure rate (0.0 to 1.0)
     */
    public double getFailureRate() {
        return 1.0 - getSuccessRate();
    }
    
    /**
     * Calculates the average operation time.
     * @return The average operation time in milliseconds
     */
    public double getAverageTime() {
        if (totalOperations == 0) {
            return 0.0;
        }
        return (double) totalTime / totalOperations;
    }
    
    /**
     * Calculates the average successful operation time.
     * @return The average successful operation time in milliseconds
     */
    public double getAverageSuccessfulTime() {
        if (successfulOperations == 0) {
            return 0.0;
        }
        // This is an approximation since we don't track successful vs failed times separately
        return (double) totalTime / totalOperations;
    }
    
    /**
     * Gets a human-readable summary of these metrics.
     * @return A summary string
     */
    public String getSummary() {
        return String.format(
            "OperationMetrics[type='%s', total=%d, success=%d (%.1f%%), avgTime=%.1fms]",
            operationType,
            totalOperations,
            successfulOperations,
            getSuccessRate() * 100,
            getAverageTime()
        );
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
}