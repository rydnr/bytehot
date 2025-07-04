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
 * Filename: CircuitBreakerStatistics.java
 *
 * Author: Claude Code
 *
 * Class name: CircuitBreakerStatistics
 *
 * Responsibilities:
 *   - Track circuit breaker operational metrics
 *   - Provide statistics for monitoring and analysis
 *   - Support circuit breaker decision making
 *
 * Collaborators:
 *   - CircuitBreaker: Provides statistics for monitoring
 *   - CircuitBreakerState: References states in statistics
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;

/**
 * Statistics and metrics for circuit breaker operation.
 * @author Claude Code
 * @since 2025-07-04
 */
public class CircuitBreakerStatistics {
    
    /**
     * The circuit breaker name.
     */
    private final String circuitBreakerName;
    
    /**
     * The current state of the circuit breaker.
     */
    private final CircuitBreakerState currentState;
    
    /**
     * The total number of requests made.
     */
    private final long totalRequests;
    
    /**
     * The number of successful requests.
     */
    private final long successfulRequests;
    
    /**
     * The number of failed requests.
     */
    private final long failedRequests;
    
    /**
     * The number of rejected requests (circuit open).
     */
    private final long rejectedRequests;
    
    /**
     * The current failure rate (0.0 to 1.0).
     */
    private final double failureRate;
    
    /**
     * The number of times the circuit has opened.
     */
    private final long circuitOpenCount;
    
    /**
     * The total time the circuit has been open.
     */
    private final Duration totalOpenTime;
    
    /**
     * The time when the circuit was last opened.
     */
    private final Instant lastOpenedAt;
    
    /**
     * The time when the circuit was last closed.
     */
    private final Instant lastClosedAt;
    
    /**
     * The number of recent failures in the sliding window.
     */
    private final int recentFailures;
    
    /**
     * The average response time for successful requests.
     */
    private final Duration averageResponseTime;
    
    /**
     * Creates a new CircuitBreakerStatistics.
     * @param circuitBreakerName The circuit breaker name
     * @param currentState The current state
     * @param totalRequests The total requests
     * @param successfulRequests The successful requests
     * @param failedRequests The failed requests
     * @param rejectedRequests The rejected requests
     * @param failureRate The failure rate
     * @param circuitOpenCount The circuit open count
     * @param totalOpenTime The total open time
     * @param lastOpenedAt The last opened time
     * @param lastClosedAt The last closed time
     * @param recentFailures The recent failures
     * @param averageResponseTime The average response time
     */
    protected CircuitBreakerStatistics(final String circuitBreakerName,
                                     final CircuitBreakerState currentState,
                                     final long totalRequests,
                                     final long successfulRequests,
                                     final long failedRequests,
                                     final long rejectedRequests,
                                     final double failureRate,
                                     final long circuitOpenCount,
                                     final Duration totalOpenTime,
                                     final Instant lastOpenedAt,
                                     final Instant lastClosedAt,
                                     final int recentFailures,
                                     final Duration averageResponseTime) {
        this.circuitBreakerName = circuitBreakerName;
        this.currentState = currentState;
        this.totalRequests = totalRequests;
        this.successfulRequests = successfulRequests;
        this.failedRequests = failedRequests;
        this.rejectedRequests = rejectedRequests;
        this.failureRate = failureRate;
        this.circuitOpenCount = circuitOpenCount;
        this.totalOpenTime = totalOpenTime;
        this.lastOpenedAt = lastOpenedAt;
        this.lastClosedAt = lastClosedAt;
        this.recentFailures = recentFailures;
        this.averageResponseTime = averageResponseTime;
    }
    
    /**
     * Creates a builder for constructing statistics.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Gets the circuit breaker name.
     * @return The circuit breaker name
     */
    public String getCircuitBreakerName() {
        return circuitBreakerName;
    }
    
    /**
     * Gets the current state.
     * @return The current state
     */
    public CircuitBreakerState getCurrentState() {
        return currentState;
    }
    
    /**
     * Gets the total number of requests.
     * @return The total requests
     */
    public long getTotalRequests() {
        return totalRequests;
    }
    
    /**
     * Gets the number of successful requests.
     * @return The successful requests
     */
    public long getSuccessfulRequests() {
        return successfulRequests;
    }
    
    /**
     * Gets the number of failed requests.
     * @return The failed requests
     */
    public long getFailedRequests() {
        return failedRequests;
    }
    
    /**
     * Gets the number of rejected requests.
     * @return The rejected requests
     */
    public long getRejectedRequests() {
        return rejectedRequests;
    }
    
    /**
     * Gets the failure rate.
     * @return The failure rate (0.0 to 1.0)
     */
    public double getFailureRate() {
        return failureRate;
    }
    
    /**
     * Gets the circuit open count.
     * @return The circuit open count
     */
    public long getCircuitOpenCount() {
        return circuitOpenCount;
    }
    
    /**
     * Gets the total open time.
     * @return The total open time
     */
    public Duration getTotalOpenTime() {
        return totalOpenTime;
    }
    
    /**
     * Gets the last opened time.
     * @return The last opened time
     */
    public Instant getLastOpenedAt() {
        return lastOpenedAt;
    }
    
    /**
     * Gets the last closed time.
     * @return The last closed time
     */
    public Instant getLastClosedAt() {
        return lastClosedAt;
    }
    
    /**
     * Gets the recent failures.
     * @return The recent failures
     */
    public int getRecentFailures() {
        return recentFailures;
    }
    
    /**
     * Gets the average response time.
     * @return The average response time
     */
    public Duration getAverageResponseTime() {
        return averageResponseTime;
    }
    
    /**
     * Calculates the success rate.
     * @return The success rate (0.0 to 1.0)
     */
    public double getSuccessRate() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) successfulRequests / totalRequests;
    }
    
    /**
     * Calculates the rejection rate.
     * @return The rejection rate (0.0 to 1.0)
     */
    public double getRejectionRate() {
        if (totalRequests == 0) {
            return 0.0;
        }
        return (double) rejectedRequests / totalRequests;
    }
    
    /**
     * Checks if the circuit breaker is healthy.
     * @return true if healthy, false otherwise
     */
    public boolean isHealthy() {
        return currentState == CircuitBreakerState.CLOSED && 
               failureRate < 0.1 && // Less than 10% failure rate
               recentFailures < 3; // Less than 3 recent failures
    }
    
    /**
     * Builder for constructing CircuitBreakerStatistics instances.
     */
    public static class Builder {
        
        private String circuitBreakerName;
        private CircuitBreakerState currentState;
        private long totalRequests;
        private long successfulRequests;
        private long failedRequests;
        private long rejectedRequests;
        private double failureRate;
        private long circuitOpenCount;
        private Duration totalOpenTime;
        private Instant lastOpenedAt;
        private Instant lastClosedAt;
        private int recentFailures;
        private Duration averageResponseTime;
        
        /**
         * Sets the circuit breaker name.
         * @param circuitBreakerName The circuit breaker name
         * @return This builder instance
         */
        public Builder circuitBreakerName(final String circuitBreakerName) {
            this.circuitBreakerName = circuitBreakerName;
            return this;
        }
        
        /**
         * Sets the current state.
         * @param currentState The current state
         * @return This builder instance
         */
        public Builder currentState(final CircuitBreakerState currentState) {
            this.currentState = currentState;
            return this;
        }
        
        /**
         * Sets the total requests.
         * @param totalRequests The total requests
         * @return This builder instance
         */
        public Builder totalRequests(final long totalRequests) {
            this.totalRequests = totalRequests;
            return this;
        }
        
        /**
         * Sets the successful requests.
         * @param successfulRequests The successful requests
         * @return This builder instance
         */
        public Builder successfulRequests(final long successfulRequests) {
            this.successfulRequests = successfulRequests;
            return this;
        }
        
        /**
         * Sets the failed requests.
         * @param failedRequests The failed requests
         * @return This builder instance
         */
        public Builder failedRequests(final long failedRequests) {
            this.failedRequests = failedRequests;
            return this;
        }
        
        /**
         * Sets the rejected requests.
         * @param rejectedRequests The rejected requests
         * @return This builder instance
         */
        public Builder rejectedRequests(final long rejectedRequests) {
            this.rejectedRequests = rejectedRequests;
            return this;
        }
        
        /**
         * Sets the failure rate.
         * @param failureRate The failure rate
         * @return This builder instance
         */
        public Builder failureRate(final double failureRate) {
            this.failureRate = failureRate;
            return this;
        }
        
        /**
         * Sets the circuit open count.
         * @param circuitOpenCount The circuit open count
         * @return This builder instance
         */
        public Builder circuitOpenCount(final long circuitOpenCount) {
            this.circuitOpenCount = circuitOpenCount;
            return this;
        }
        
        /**
         * Sets the total open time.
         * @param totalOpenTime The total open time
         * @return This builder instance
         */
        public Builder totalOpenTime(final Duration totalOpenTime) {
            this.totalOpenTime = totalOpenTime;
            return this;
        }
        
        /**
         * Sets the last opened time.
         * @param lastOpenedAt The last opened time
         * @return This builder instance
         */
        public Builder lastOpenedAt(final Instant lastOpenedAt) {
            this.lastOpenedAt = lastOpenedAt;
            return this;
        }
        
        /**
         * Sets the last closed time.
         * @param lastClosedAt The last closed time
         * @return This builder instance
         */
        public Builder lastClosedAt(final Instant lastClosedAt) {
            this.lastClosedAt = lastClosedAt;
            return this;
        }
        
        /**
         * Sets the recent failures.
         * @param recentFailures The recent failures
         * @return This builder instance
         */
        public Builder recentFailures(final int recentFailures) {
            this.recentFailures = recentFailures;
            return this;
        }
        
        /**
         * Sets the average response time.
         * @param averageResponseTime The average response time
         * @return This builder instance
         */
        public Builder averageResponseTime(final Duration averageResponseTime) {
            this.averageResponseTime = averageResponseTime;
            return this;
        }
        
        /**
         * Builds the CircuitBreakerStatistics instance.
         * @return A new CircuitBreakerStatistics instance
         */
        public CircuitBreakerStatistics build() {
            return new CircuitBreakerStatistics(
                circuitBreakerName,
                currentState,
                totalRequests,
                successfulRequests,
                failedRequests,
                rejectedRequests,
                failureRate,
                circuitOpenCount,
                totalOpenTime,
                lastOpenedAt,
                lastClosedAt,
                recentFailures,
                averageResponseTime
            );
        }
    }
}