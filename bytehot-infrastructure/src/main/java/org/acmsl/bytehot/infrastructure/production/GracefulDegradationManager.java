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
 * Filename: GracefulDegradationManager.java
 *
 * Author: Claude Code
 *
 * Class name: GracefulDegradationManager
 *
 * Responsibilities:
 *   - Manage degraded operation modes when system health is compromised
 *   - Execute operations with fallback strategies
 *   - Monitor system health and adjust degradation levels
 *   - Provide graceful fallbacks for non-critical operations
 *
 * Collaborators:
 *   - DegradationStrategy: Defines how to handle operations in degraded mode
 *   - SystemHealthMonitor: Monitors overall system health status
 *   - OperationResult: Represents the result of executing an operation
 *   - HealthStatus: Represents current system health state
 */
package org.acmsl.bytehot.infrastructure.production;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages graceful degradation of ByteHot operations when system health is compromised.
 * @author Claude Code
 * @since 2025-07-04
 */
public class GracefulDegradationManager {
    
    /**
     * Map of degradation strategies by operation type.
     */
    private final Map<String, DegradationStrategy> strategies;
    
    /**
     * System health monitor for determining degradation level.
     */
    private final SystemHealthMonitor healthMonitor;
    
    /**
     * Default degradation strategy for operations without specific strategies.
     */
    private final DegradationStrategy defaultStrategy;
    
    /**
     * Creates a new GracefulDegradationManager.
     * @param healthMonitor The system health monitor
     * @param defaultStrategy The default degradation strategy
     */
    public GracefulDegradationManager(final SystemHealthMonitor healthMonitor,
                                     final DegradationStrategy defaultStrategy) {
        this.healthMonitor = healthMonitor;
        this.defaultStrategy = defaultStrategy;
        this.strategies = new ConcurrentHashMap<>();
        
        initializeDefaultStrategies();
    }
    
    /**
     * Executes an operation with graceful degradation if system health is compromised.
     * @param operation The operation to execute
     * @return The operation result
     */
    public OperationResult executeWithDegradation(final Operation operation) {
        HealthStatus health = healthMonitor.getCurrentHealth();
        
        if (health.isHealthy()) {
            // System is healthy, execute normally
            return operation.execute();
        }
        
        // System is degraded, apply degradation strategy
        DegradationStrategy strategy = getStrategyForOperation(operation);
        
        if (strategy.canHandle(operation.getType())) {
            return strategy.executeInDegradedMode(operation, health);
        } else {
            // No strategy available, fail gracefully
            return OperationResult.degradedFailure(
                "Operation cannot be executed in current system state",
                health.getDegradationLevel()
            );
        }
    }
    
    /**
     * Registers a degradation strategy for a specific operation type.
     * @param operationType The operation type
     * @param strategy The degradation strategy
     */
    public void registerStrategy(final String operationType, final DegradationStrategy strategy) {
        strategies.put(operationType, strategy);
    }
    
    /**
     * Gets the current degradation level based on system health.
     * @return The current degradation level
     */
    public DegradationLevel getCurrentDegradationLevel() {
        HealthStatus health = healthMonitor.getCurrentHealth();
        return health.getDegradationLevel();
    }
    
    /**
     * Checks if the system is currently operating in degraded mode.
     * @return true if degraded, false if healthy
     */
    public boolean isDegraded() {
        return !healthMonitor.getCurrentHealth().isHealthy();
    }
    
    /**
     * Gets degradation statistics for monitoring and reporting.
     * @return The degradation statistics
     */
    public DegradationStatistics getDegradationStatistics() {
        return DegradationStatistics.builder()
            .currentLevel(getCurrentDegradationLevel())
            .totalDegradedOperations(getTotalDegradedOperations())
            .successfulDegradedOperations(getSuccessfulDegradedOperations())
            .degradationStartTime(getDegradationStartTime())
            .build();
    }
    
    /**
     * Gets the degradation strategy for the specified operation.
     * @param operation The operation
     * @return The appropriate degradation strategy
     */
    protected DegradationStrategy getStrategyForOperation(final Operation operation) {
        DegradationStrategy strategy = strategies.get(operation.getType());
        return strategy != null ? strategy : defaultStrategy;
    }
    
    /**
     * Initializes default degradation strategies for common operation types.
     */
    protected void initializeDefaultStrategies() {
        // Hot-swap degradation: Skip non-critical hot-swaps
        registerStrategy("hotswap", new HotSwapDegradationStrategy());
        
        // File watching degradation: Reduce polling frequency
        registerStrategy("file-watching", new FileWatchingDegradationStrategy());
        
        // Metrics collection degradation: Sample metrics instead of collecting all
        registerStrategy("metrics", new MetricsDegradationStrategy());
        
        // Notification degradation: Queue notifications instead of sending immediately
        registerStrategy("notification", new NotificationDegradationStrategy());
    }
    
    /**
     * Gets the total number of operations executed in degraded mode.
     * @return The total degraded operations count
     */
    protected long getTotalDegradedOperations() {
        // Implementation would track this metric
        return 0;
    }
    
    /**
     * Gets the number of successfully executed degraded operations.
     * @return The successful degraded operations count
     */
    protected long getSuccessfulDegradedOperations() {
        // Implementation would track this metric
        return 0;
    }
    
    /**
     * Gets the time when degradation mode was first entered.
     * @return The degradation start time, or null if not degraded
     */
    protected java.time.Instant getDegradationStartTime() {
        // Implementation would track this metric
        return null;
    }
}