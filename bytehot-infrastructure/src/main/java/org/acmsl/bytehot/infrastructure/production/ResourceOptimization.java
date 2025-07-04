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
 * Filename: ResourceOptimization.java
 *
 * Author: Claude Code
 *
 * Class name: ResourceOptimization
 *
 * Responsibilities:
 *   - Represent a resource optimization operation
 *   - Track optimization progress and results
 *   - Provide optimization metadata and status
 *
 * Collaborators:
 *   - ResourceManager: Creates and manages optimizations
 *   - MemoryOptimizer: Creates memory optimizations
 *   - PerformanceOptimizer: Creates performance optimizations
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Represents a resource optimization operation with its status and metadata.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ResourceOptimization {
    
    /**
     * Unique identifier for this optimization.
     */
    private final String id;
    
    /**
     * The type of resource optimization.
     */
    private final ResourceOptimizationType type;
    
    /**
     * The optimization strategy used.
     */
    private final String strategy;
    
    /**
     * The reason for this optimization.
     */
    private final String reason;
    
    /**
     * The time when optimization started.
     */
    private final Instant startTime;
    
    /**
     * The resource snapshot at optimization start.
     */
    private final ResourceSnapshot resourceSnapshot;
    
    /**
     * The estimated duration for this optimization.
     */
    private final Duration estimatedDuration;
    
    /**
     * The time when optimization completed.
     */
    private volatile Instant completionTime;
    
    /**
     * The optimization status.
     */
    private volatile OptimizationStatus status;
    
    /**
     * The optimization result message.
     */
    private volatile String resultMessage;
    
    /**
     * Whether the optimization was successful.
     */
    private volatile boolean successful;
    
    /**
     * Creates a new ResourceOptimization.
     * @param id The optimization ID
     * @param type The optimization type
     * @param strategy The optimization strategy
     * @param reason The reason for optimization
     * @param startTime The start time
     * @param resourceSnapshot The resource snapshot
     * @param estimatedDuration The estimated duration
     */
    protected ResourceOptimization(final String id,
                                 final ResourceOptimizationType type,
                                 final String strategy,
                                 final String reason,
                                 final Instant startTime,
                                 final ResourceSnapshot resourceSnapshot,
                                 final Duration estimatedDuration) {
        this.id = id;
        this.type = type;
        this.strategy = strategy;
        this.reason = reason;
        this.startTime = startTime;
        this.resourceSnapshot = resourceSnapshot;
        this.estimatedDuration = estimatedDuration;
        this.status = OptimizationStatus.RUNNING;
        this.successful = false;
    }
    
    /**
     * Creates a builder for constructing resource optimizations.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Marks this optimization as completed successfully.
     */
    public void markCompleted() {
        markCompleted(true, "Optimization completed successfully");
    }
    
    /**
     * Marks this optimization as completed with the specified result.
     * @param successful Whether the optimization was successful
     * @param resultMessage The result message
     */
    public void markCompleted(final boolean successful, final String resultMessage) {
        this.completionTime = Instant.now();
        this.successful = successful;
        this.resultMessage = resultMessage;
        this.status = successful ? OptimizationStatus.COMPLETED : OptimizationStatus.FAILED;
    }
    
    /**
     * Marks this optimization as failed.
     * @param errorMessage The error message
     */
    public void markFailed(final String errorMessage) {
        markCompleted(false, errorMessage);
    }
    
    /**
     * Stops this optimization.
     */
    public void stop() {
        if (status == OptimizationStatus.RUNNING) {
            markCompleted(false, "Optimization stopped");
            status = OptimizationStatus.STOPPED;
        }
    }
    
    /**
     * Gets the optimization ID.
     * @return The optimization ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the optimization type.
     * @return The optimization type
     */
    public ResourceOptimizationType getType() {
        return type;
    }
    
    /**
     * Gets the optimization strategy.
     * @return The optimization strategy
     */
    public String getStrategy() {
        return strategy;
    }
    
    /**
     * Gets the reason for optimization.
     * @return The reason
     */
    public String getReason() {
        return reason;
    }
    
    /**
     * Gets the start time.
     * @return The start time
     */
    public Instant getStartTime() {
        return startTime;
    }
    
    /**
     * Gets the resource snapshot.
     * @return The resource snapshot
     */
    public ResourceSnapshot getResourceSnapshot() {
        return resourceSnapshot;
    }
    
    /**
     * Gets the estimated duration.
     * @return The estimated duration
     */
    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }
    
    /**
     * Gets the completion time.
     * @return The completion time, if completed
     */
    public Optional<Instant> getCompletionTime() {
        return Optional.ofNullable(completionTime);
    }
    
    /**
     * Gets the optimization status.
     * @return The optimization status
     */
    public OptimizationStatus getStatus() {
        return status;
    }
    
    /**
     * Gets the result message.
     * @return The result message, if available
     */
    public Optional<String> getResultMessage() {
        return Optional.ofNullable(resultMessage);
    }
    
    /**
     * Checks if the optimization was successful.
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }
    
    /**
     * Checks if the optimization is completed.
     * @return true if completed, false otherwise
     */
    public boolean isCompleted() {
        return status == OptimizationStatus.COMPLETED || status == OptimizationStatus.FAILED || status == OptimizationStatus.STOPPED;
    }
    
    /**
     * Checks if the optimization is currently running.
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return status == OptimizationStatus.RUNNING;
    }
    
    /**
     * Checks if the optimization has expired (running longer than estimated).
     * @return true if expired, false otherwise
     */
    public boolean hasExpired() {
        if (isCompleted()) {
            return false;
        }
        
        Duration actualDuration = Duration.between(startTime, Instant.now());
        return actualDuration.compareTo(estimatedDuration.multipliedBy(2)) > 0; // Expired if taking more than 2x estimated time
    }
    
    /**
     * Gets the actual duration of the optimization.
     * @return The actual duration
     */
    public Duration getActualDuration() {
        Instant endTime = completionTime != null ? completionTime : Instant.now();
        return Duration.between(startTime, endTime);
    }
    
    /**
     * Gets the progress of the optimization as a percentage.
     * @return The progress percentage (0.0 to 1.0)
     */
    public double getProgress() {
        if (isCompleted()) {
            return 1.0;
        }
        
        Duration actualDuration = getActualDuration();
        if (actualDuration.compareTo(estimatedDuration) >= 0) {
            return 0.99; // Almost complete if we're at or past estimated duration
        }
        
        return (double) actualDuration.toMillis() / estimatedDuration.toMillis();
    }
    
    /**
     * Gets a summary of this optimization.
     * @return A summary string
     */
    public String getSummary() {
        return String.format(
            "ResourceOptimization[id=%s, type=%s, strategy=%s, status=%s, progress=%.1f%%, duration=%s]",
            id,
            type,
            strategy,
            status,
            getProgress() * 100,
            getActualDuration()
        );
    }
    
    @Override
    public String toString() {
        return getSummary();
    }
    
    /**
     * Builder for constructing ResourceOptimization instances.
     */
    public static class Builder {
        
        private String id;
        private ResourceOptimizationType type;
        private String strategy;
        private String reason;
        private Instant startTime;
        private ResourceSnapshot resourceSnapshot;
        private Duration estimatedDuration;
        
        /**
         * Sets the optimization ID.
         * @param id The optimization ID
         * @return This builder instance
         */
        public Builder id(final String id) {
            this.id = id;
            return this;
        }
        
        /**
         * Sets the optimization type.
         * @param type The optimization type
         * @return This builder instance
         */
        public Builder type(final ResourceOptimizationType type) {
            this.type = type;
            return this;
        }
        
        /**
         * Sets the optimization strategy.
         * @param strategy The optimization strategy
         * @return This builder instance
         */
        public Builder strategy(final String strategy) {
            this.strategy = strategy;
            return this;
        }
        
        /**
         * Sets the reason for optimization.
         * @param reason The reason
         * @return This builder instance
         */
        public Builder reason(final String reason) {
            this.reason = reason;
            return this;
        }
        
        /**
         * Sets the start time.
         * @param startTime The start time
         * @return This builder instance
         */
        public Builder startTime(final Instant startTime) {
            this.startTime = startTime;
            return this;
        }
        
        /**
         * Sets the resource snapshot.
         * @param resourceSnapshot The resource snapshot
         * @return This builder instance
         */
        public Builder resourceSnapshot(final ResourceSnapshot resourceSnapshot) {
            this.resourceSnapshot = resourceSnapshot;
            return this;
        }
        
        /**
         * Sets the estimated duration.
         * @param estimatedDuration The estimated duration
         * @return This builder instance
         */
        public Builder estimatedDuration(final Duration estimatedDuration) {
            this.estimatedDuration = estimatedDuration;
            return this;
        }
        
        /**
         * Builds the ResourceOptimization instance.
         * @return A new ResourceOptimization instance
         */
        public ResourceOptimization build() {
            return new ResourceOptimization(
                id,
                type,
                strategy,
                reason,
                startTime,
                resourceSnapshot,
                estimatedDuration
            );
        }
    }
}