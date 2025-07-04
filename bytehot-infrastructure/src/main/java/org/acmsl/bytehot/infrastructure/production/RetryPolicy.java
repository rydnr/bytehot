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
 * Filename: RetryPolicy.java
 *
 * Author: Claude Code
 *
 * Class name: RetryPolicy
 *
 * Responsibilities:
 *   - Define retry behavior and timing strategies
 *   - Support exponential backoff and other patterns
 *   - Configure retry limits and conditions
 *
 * Collaborators:
 *   - RecoveryAttempt: Uses policy for retry decisions
 *   - RecoveryManager: Applies policies to recovery attempts
 *   - RecoveryStrategy: References policies for timing
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Defines retry policy and backoff strategies for recovery attempts.
 * @author Claude Code
 * @since 2025-07-04
 */
public class RetryPolicy {
    
    /**
     * Default maximum number of retry attempts.
     */
    public static final int DEFAULT_MAX_ATTEMPTS = 3;
    
    /**
     * Default initial wait time between retries.
     */
    public static final Duration DEFAULT_INITIAL_WAIT = Duration.ofSeconds(1);
    
    /**
     * Default maximum wait time between retries.
     */
    public static final Duration DEFAULT_MAX_WAIT = Duration.ofMinutes(1);
    
    /**
     * Default total time limit for all retry attempts.
     */
    public static final Duration DEFAULT_MAX_TOTAL_TIME = Duration.ofMinutes(5);
    
    /**
     * Default exponential backoff multiplier.
     */
    public static final double DEFAULT_BACKOFF_MULTIPLIER = 2.0;
    
    /**
     * Maximum number of retry attempts.
     */
    private final int maxAttempts;
    
    /**
     * Initial wait time between retries.
     */
    private final Duration initialWait;
    
    /**
     * Maximum wait time between retries.
     */
    private final Duration maxWait;
    
    /**
     * Total time limit for all retry attempts.
     */
    private final Duration maxTotalTime;
    
    /**
     * Backoff strategy for calculating wait times.
     */
    private final BackoffStrategy backoffStrategy;
    
    /**
     * Whether to add jitter to wait times.
     */
    private final boolean jitterEnabled;
    
    /**
     * Jitter factor (0.0 to 1.0) for randomizing wait times.
     */
    private final double jitterFactor;
    
    /**
     * Creates a new RetryPolicy.
     * @param maxAttempts The maximum attempts
     * @param initialWait The initial wait time
     * @param maxWait The maximum wait time
     * @param maxTotalTime The maximum total time
     * @param backoffStrategy The backoff strategy
     * @param jitterEnabled Whether jitter is enabled
     * @param jitterFactor The jitter factor
     */
    protected RetryPolicy(final int maxAttempts,
                        final Duration initialWait,
                        final Duration maxWait,
                        final Duration maxTotalTime,
                        final BackoffStrategy backoffStrategy,
                        final boolean jitterEnabled,
                        final double jitterFactor) {
        this.maxAttempts = maxAttempts;
        this.initialWait = initialWait;
        this.maxWait = maxWait;
        this.maxTotalTime = maxTotalTime;
        this.backoffStrategy = backoffStrategy;
        this.jitterEnabled = jitterEnabled;
        this.jitterFactor = jitterFactor;
    }
    
    /**
     * Creates a default retry policy.
     * @return A default retry policy
     */
    public static RetryPolicy defaultPolicy() {
        return new Builder()
            .maxAttempts(DEFAULT_MAX_ATTEMPTS)
            .initialWait(DEFAULT_INITIAL_WAIT)
            .maxWait(DEFAULT_MAX_WAIT)
            .maxTotalTime(DEFAULT_MAX_TOTAL_TIME)
            .exponentialBackoff(DEFAULT_BACKOFF_MULTIPLIER)
            .build();
    }
    
    /**
     * Creates a no-retry policy.
     * @return A policy that allows no retries
     */
    public static RetryPolicy noRetry() {
        return new Builder()
            .maxAttempts(1)
            .initialWait(Duration.ZERO)
            .maxWait(Duration.ZERO)
            .maxTotalTime(Duration.ZERO)
            .fixedBackoff()
            .build();
    }
    
    /**
     * Creates an aggressive retry policy.
     * @return An aggressive retry policy
     */
    public static RetryPolicy aggressive() {
        return new Builder()
            .maxAttempts(10)
            .initialWait(Duration.ofMillis(100))
            .maxWait(Duration.ofSeconds(10))
            .maxTotalTime(Duration.ofMinutes(2))
            .exponentialBackoff(1.5)
            .jitter(0.2)
            .build();
    }
    
    /**
     * Creates a conservative retry policy.
     * @return A conservative retry policy
     */
    public static RetryPolicy conservative() {
        return new Builder()
            .maxAttempts(2)
            .initialWait(Duration.ofSeconds(5))
            .maxWait(Duration.ofMinutes(2))
            .maxTotalTime(Duration.ofMinutes(10))
            .exponentialBackoff(3.0)
            .build();
    }
    
    /**
     * Creates a builder for constructing retry policies.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Calculates the wait time for the given attempt number.
     * @param attemptNumber The attempt number (1-based)
     * @return The wait time before the next attempt
     */
    public Duration getWaitTime(final int attemptNumber) {
        if (attemptNumber <= 1) {
            return Duration.ZERO; // No wait before first attempt
        }
        
        Duration waitTime = backoffStrategy.calculateWaitTime(
            attemptNumber - 1, // Convert to 0-based for calculation
            initialWait,
            maxWait
        );
        
        // Apply jitter if enabled
        if (jitterEnabled) {
            waitTime = applyJitter(waitTime);
        }
        
        return waitTime;
    }
    
    /**
     * Gets the maximum number of attempts.
     * @return The maximum attempts
     */
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    /**
     * Gets the initial wait time.
     * @return The initial wait time
     */
    public Duration getInitialWait() {
        return initialWait;
    }
    
    /**
     * Gets the maximum wait time.
     * @return The maximum wait time
     */
    public Duration getMaxWait() {
        return maxWait;
    }
    
    /**
     * Gets the maximum total time.
     * @return The maximum total time
     */
    public Duration getMaxTotalTime() {
        return maxTotalTime;
    }
    
    /**
     * Gets the backoff strategy.
     * @return The backoff strategy
     */
    public BackoffStrategy getBackoffStrategy() {
        return backoffStrategy;
    }
    
    /**
     * Checks if jitter is enabled.
     * @return true if jitter is enabled, false otherwise
     */
    public boolean isJitterEnabled() {
        return jitterEnabled;
    }
    
    /**
     * Gets the jitter factor.
     * @return The jitter factor
     */
    public double getJitterFactor() {
        return jitterFactor;
    }
    
    /**
     * Applies jitter to the given wait time.
     * @param waitTime The original wait time
     * @return The wait time with jitter applied
     */
    protected Duration applyJitter(final Duration waitTime) {
        double jitterRange = waitTime.toMillis() * jitterFactor;
        double jitter = ThreadLocalRandom.current().nextDouble(-jitterRange, jitterRange);
        long jitteredMs = Math.max(0, waitTime.toMillis() + (long) jitter);
        
        return Duration.ofMillis(jitteredMs);
    }
    
    /**
     * Builder for constructing RetryPolicy instances.
     */
    public static class Builder {
        
        private int maxAttempts = DEFAULT_MAX_ATTEMPTS;
        private Duration initialWait = DEFAULT_INITIAL_WAIT;
        private Duration maxWait = DEFAULT_MAX_WAIT;
        private Duration maxTotalTime = DEFAULT_MAX_TOTAL_TIME;
        private BackoffStrategy backoffStrategy = BackoffStrategy.exponential(DEFAULT_BACKOFF_MULTIPLIER);
        private boolean jitterEnabled = false;
        private double jitterFactor = 0.1;
        
        /**
         * Sets the maximum number of attempts.
         * @param maxAttempts The maximum attempts
         * @return This builder instance
         */
        public Builder maxAttempts(final int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }
        
        /**
         * Sets the initial wait time.
         * @param initialWait The initial wait time
         * @return This builder instance
         */
        public Builder initialWait(final Duration initialWait) {
            this.initialWait = initialWait;
            return this;
        }
        
        /**
         * Sets the maximum wait time.
         * @param maxWait The maximum wait time
         * @return This builder instance
         */
        public Builder maxWait(final Duration maxWait) {
            this.maxWait = maxWait;
            return this;
        }
        
        /**
         * Sets the maximum total time.
         * @param maxTotalTime The maximum total time
         * @return This builder instance
         */
        public Builder maxTotalTime(final Duration maxTotalTime) {
            this.maxTotalTime = maxTotalTime;
            return this;
        }
        
        /**
         * Sets fixed backoff strategy.
         * @return This builder instance
         */
        public Builder fixedBackoff() {
            this.backoffStrategy = BackoffStrategy.fixed();
            return this;
        }
        
        /**
         * Sets linear backoff strategy.
         * @return This builder instance
         */
        public Builder linearBackoff() {
            this.backoffStrategy = BackoffStrategy.linear();
            return this;
        }
        
        /**
         * Sets exponential backoff strategy.
         * @param multiplier The backoff multiplier
         * @return This builder instance
         */
        public Builder exponentialBackoff(final double multiplier) {
            this.backoffStrategy = BackoffStrategy.exponential(multiplier);
            return this;
        }
        
        /**
         * Enables jitter with the specified factor.
         * @param jitterFactor The jitter factor (0.0 to 1.0)
         * @return This builder instance
         */
        public Builder jitter(final double jitterFactor) {
            this.jitterEnabled = true;
            this.jitterFactor = jitterFactor;
            return this;
        }
        
        /**
         * Disables jitter.
         * @return This builder instance
         */
        public Builder noJitter() {
            this.jitterEnabled = false;
            return this;
        }
        
        /**
         * Builds the RetryPolicy instance.
         * @return A new RetryPolicy instance
         */
        public RetryPolicy build() {
            return new RetryPolicy(
                maxAttempts,
                initialWait,
                maxWait,
                maxTotalTime,
                backoffStrategy,
                jitterEnabled,
                jitterFactor
            );
        }
    }
}