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
 * Filename: CircuitBreakerConfiguration.java
 *
 * Author: Claude Code
 *
 * Class name: CircuitBreakerConfiguration
 *
 * Responsibilities:
 *   - Define circuit breaker operational parameters
 *   - Configure failure thresholds and timing
 *   - Support customizable circuit breaker behavior
 *
 * Collaborators:
 *   - CircuitBreaker: Uses this configuration for operation
 *   - CircuitBreakerStatistics: References configuration for metrics
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;

/**
 * Configuration for circuit breaker behavior.
 * @author Claude Code
 * @since 2025-07-04
 */
public class CircuitBreakerConfiguration {
    
    /**
     * Default failure threshold for opening the circuit.
     */
    public static final int DEFAULT_FAILURE_THRESHOLD = 5;
    
    /**
     * Default success threshold for closing the circuit.
     */
    public static final int DEFAULT_SUCCESS_THRESHOLD = 3;
    
    /**
     * Default timeout for staying in open state.
     */
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    
    /**
     * Default window size for failure rate calculation.
     */
    public static final int DEFAULT_WINDOW_SIZE = 10;
    
    /**
     * Default minimum number of requests before calculating failure rate.
     */
    public static final int DEFAULT_MIN_REQUESTS = 5;
    
    /**
     * Number of failures required to open the circuit.
     */
    private final int failureThreshold;
    
    /**
     * Number of successes required to close the circuit from half-open state.
     */
    private final int successThreshold;
    
    /**
     * Duration to wait before transitioning from open to half-open state.
     */
    private final Duration timeout;
    
    /**
     * Size of the sliding window for failure rate calculation.
     */
    private final int windowSize;
    
    /**
     * Minimum number of requests before failure rate is calculated.
     */
    private final int minimumRequests;
    
    /**
     * Failure rate threshold (0.0 to 1.0) for opening the circuit.
     */
    private final double failureRateThreshold;
    
    /**
     * Creates a new CircuitBreakerConfiguration.
     * @param failureThreshold The failure threshold
     * @param successThreshold The success threshold
     * @param timeout The timeout duration
     * @param windowSize The window size
     * @param minimumRequests The minimum requests
     * @param failureRateThreshold The failure rate threshold
     */
    protected CircuitBreakerConfiguration(final int failureThreshold,
                                        final int successThreshold,
                                        final Duration timeout,
                                        final int windowSize,
                                        final int minimumRequests,
                                        final double failureRateThreshold) {
        this.failureThreshold = failureThreshold;
        this.successThreshold = successThreshold;
        this.timeout = timeout;
        this.windowSize = windowSize;
        this.minimumRequests = minimumRequests;
        this.failureRateThreshold = failureRateThreshold;
    }
    
    /**
     * Creates a default configuration.
     * @return A default circuit breaker configuration
     */
    public static CircuitBreakerConfiguration defaultConfiguration() {
        return new Builder()
            .failureThreshold(DEFAULT_FAILURE_THRESHOLD)
            .successThreshold(DEFAULT_SUCCESS_THRESHOLD)
            .timeout(DEFAULT_TIMEOUT)
            .windowSize(DEFAULT_WINDOW_SIZE)
            .minimumRequests(DEFAULT_MIN_REQUESTS)
            .failureRateThreshold(0.5)
            .build();
    }
    
    /**
     * Creates a conservative configuration with higher thresholds.
     * @return A conservative circuit breaker configuration
     */
    public static CircuitBreakerConfiguration conservativeConfiguration() {
        return new Builder()
            .failureThreshold(10)
            .successThreshold(5)
            .timeout(Duration.ofMinutes(1))
            .windowSize(20)
            .minimumRequests(10)
            .failureRateThreshold(0.7)
            .build();
    }
    
    /**
     * Creates an aggressive configuration with lower thresholds.
     * @return An aggressive circuit breaker configuration
     */
    public static CircuitBreakerConfiguration aggressiveConfiguration() {
        return new Builder()
            .failureThreshold(3)
            .successThreshold(2)
            .timeout(Duration.ofSeconds(10))
            .windowSize(5)
            .minimumRequests(3)
            .failureRateThreshold(0.3)
            .build();
    }
    
    /**
     * Creates a builder for constructing configurations.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Gets the failure threshold.
     * @return The failure threshold
     */
    public int getFailureThreshold() {
        return failureThreshold;
    }
    
    /**
     * Gets the success threshold.
     * @return The success threshold
     */
    public int getSuccessThreshold() {
        return successThreshold;
    }
    
    /**
     * Gets the timeout duration.
     * @return The timeout duration
     */
    public Duration getTimeout() {
        return timeout;
    }
    
    /**
     * Gets the window size.
     * @return The window size
     */
    public int getWindowSize() {
        return windowSize;
    }
    
    /**
     * Gets the minimum requests.
     * @return The minimum requests
     */
    public int getMinimumRequests() {
        return minimumRequests;
    }
    
    /**
     * Gets the failure rate threshold.
     * @return The failure rate threshold
     */
    public double getFailureRateThreshold() {
        return failureRateThreshold;
    }
    
    /**
     * Builder for constructing CircuitBreakerConfiguration instances.
     */
    public static class Builder {
        
        private int failureThreshold = DEFAULT_FAILURE_THRESHOLD;
        private int successThreshold = DEFAULT_SUCCESS_THRESHOLD;
        private Duration timeout = DEFAULT_TIMEOUT;
        private int windowSize = DEFAULT_WINDOW_SIZE;
        private int minimumRequests = DEFAULT_MIN_REQUESTS;
        private double failureRateThreshold = 0.5;
        
        /**
         * Sets the failure threshold.
         * @param failureThreshold The failure threshold
         * @return This builder instance
         */
        public Builder failureThreshold(final int failureThreshold) {
            this.failureThreshold = failureThreshold;
            return this;
        }
        
        /**
         * Sets the success threshold.
         * @param successThreshold The success threshold
         * @return This builder instance
         */
        public Builder successThreshold(final int successThreshold) {
            this.successThreshold = successThreshold;
            return this;
        }
        
        /**
         * Sets the timeout duration.
         * @param timeout The timeout duration
         * @return This builder instance
         */
        public Builder timeout(final Duration timeout) {
            this.timeout = timeout;
            return this;
        }
        
        /**
         * Sets the window size.
         * @param windowSize The window size
         * @return This builder instance
         */
        public Builder windowSize(final int windowSize) {
            this.windowSize = windowSize;
            return this;
        }
        
        /**
         * Sets the minimum requests.
         * @param minimumRequests The minimum requests
         * @return This builder instance
         */
        public Builder minimumRequests(final int minimumRequests) {
            this.minimumRequests = minimumRequests;
            return this;
        }
        
        /**
         * Sets the failure rate threshold.
         * @param failureRateThreshold The failure rate threshold
         * @return This builder instance
         */
        public Builder failureRateThreshold(final double failureRateThreshold) {
            this.failureRateThreshold = failureRateThreshold;
            return this;
        }
        
        /**
         * Builds the CircuitBreakerConfiguration instance.
         * @return A new CircuitBreakerConfiguration instance
         */
        public CircuitBreakerConfiguration build() {
            return new CircuitBreakerConfiguration(
                failureThreshold,
                successThreshold,
                timeout,
                windowSize,
                minimumRequests,
                failureRateThreshold
            );
        }
    }
}