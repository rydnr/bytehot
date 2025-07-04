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
 * Filename: RecoveryResult.java
 *
 * Author: Claude Code
 *
 * Class name: RecoveryResult
 *
 * Responsibilities:
 *   - Represent the result of a recovery attempt
 *   - Track success/failure status and recovery metadata
 *   - Provide context for further recovery decisions
 *
 * Collaborators:
 *   - RecoveryManager: Creates and consumes recovery results
 *   - RecoveryStrategy: Produces recovery results
 *   - IncidentReporter: Uses results for incident reporting
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Represents the result of a recovery attempt.
 * @author Claude Code
 * @since 2025-07-04
 */
public class RecoveryResult {
    
    /**
     * Whether the recovery was successful.
     */
    private final boolean successful;
    
    /**
     * The message describing the recovery outcome.
     */
    private final String message;
    
    /**
     * The timestamp when the recovery was attempted.
     */
    private final Instant timestamp;
    
    /**
     * The duration of the recovery attempt.
     */
    private final Duration duration;
    
    /**
     * The number of attempts made during recovery.
     */
    private final int attemptCount;
    
    /**
     * The recovery strategy used.
     */
    private final String strategyUsed;
    
    /**
     * Any exception that occurred during recovery.
     */
    private final Throwable recoveryException;
    
    /**
     * Creates a new RecoveryResult.
     * @param successful Whether the recovery was successful
     * @param message The recovery outcome message
     * @param timestamp The recovery timestamp
     * @param duration The recovery duration
     * @param attemptCount The number of attempts made
     * @param strategyUsed The recovery strategy used
     * @param recoveryException Any exception during recovery
     */
    protected RecoveryResult(final boolean successful,
                           final String message,
                           final Instant timestamp,
                           final Duration duration,
                           final int attemptCount,
                           final String strategyUsed,
                           final Throwable recoveryException) {
        this.successful = successful;
        this.message = message;
        this.timestamp = timestamp;
        this.duration = duration;
        this.attemptCount = attemptCount;
        this.strategyUsed = strategyUsed;
        this.recoveryException = recoveryException;
    }
    
    /**
     * Creates a successful recovery result.
     * @param message The success message
     * @return A successful recovery result
     */
    public static RecoveryResult success(final String message) {
        return new Builder()
            .successful(true)
            .message(message)
            .timestamp(Instant.now())
            .build();
    }
    
    /**
     * Creates a successful recovery result with detailed information.
     * @param message The success message
     * @param duration The recovery duration
     * @param attemptCount The number of attempts
     * @param strategyUsed The strategy used
     * @return A successful recovery result
     */
    public static RecoveryResult success(final String message,
                                       final Duration duration,
                                       final int attemptCount,
                                       final String strategyUsed) {
        return new Builder()
            .successful(true)
            .message(message)
            .timestamp(Instant.now())
            .duration(duration)
            .attemptCount(attemptCount)
            .strategyUsed(strategyUsed)
            .build();
    }
    
    /**
     * Creates a failed recovery result.
     * @param message The failure message
     * @return A failed recovery result
     */
    public static RecoveryResult failed(final String message) {
        return new Builder()
            .successful(false)
            .message(message)
            .timestamp(Instant.now())
            .build();
    }
    
    /**
     * Creates a failed recovery result with exception.
     * @param message The failure message
     * @param exception The exception that caused the failure
     * @return A failed recovery result
     */
    public static RecoveryResult failed(final String message, final Throwable exception) {
        return new Builder()
            .successful(false)
            .message(message)
            .timestamp(Instant.now())
            .recoveryException(exception)
            .build();
    }
    
    /**
     * Creates a skipped recovery result.
     * @param reason The reason for skipping recovery
     * @return A skipped recovery result
     */
    public static RecoveryResult skipped(final String reason) {
        return new Builder()
            .successful(false)
            .message("Recovery skipped: " + reason)
            .timestamp(Instant.now())
            .build();
    }
    
    /**
     * Creates a builder for constructing recovery results.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Checks if the recovery was successful.
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return successful;
    }
    
    /**
     * Gets the recovery outcome message.
     * @return The recovery message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the recovery timestamp.
     * @return The recovery timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the recovery duration.
     * @return The recovery duration, if available
     */
    public Optional<Duration> getDuration() {
        return Optional.ofNullable(duration);
    }
    
    /**
     * Gets the number of attempts made.
     * @return The attempt count
     */
    public int getAttemptCount() {
        return attemptCount;
    }
    
    /**
     * Gets the recovery strategy used.
     * @return The strategy name, if available
     */
    public Optional<String> getStrategyUsed() {
        return Optional.ofNullable(strategyUsed);
    }
    
    /**
     * Gets the recovery exception.
     * @return The recovery exception, if any
     */
    public Optional<Throwable> getRecoveryException() {
        return Optional.ofNullable(recoveryException);
    }
    
    /**
     * Builder for constructing RecoveryResult instances.
     */
    public static class Builder {
        
        private boolean successful;
        private String message;
        private Instant timestamp;
        private Duration duration;
        private int attemptCount;
        private String strategyUsed;
        private Throwable recoveryException;
        
        /**
         * Sets the success status.
         * @param successful Whether the recovery was successful
         * @return This builder instance
         */
        public Builder successful(final boolean successful) {
            this.successful = successful;
            return this;
        }
        
        /**
         * Sets the recovery message.
         * @param message The recovery message
         * @return This builder instance
         */
        public Builder message(final String message) {
            this.message = message;
            return this;
        }
        
        /**
         * Sets the recovery timestamp.
         * @param timestamp The recovery timestamp
         * @return This builder instance
         */
        public Builder timestamp(final Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Sets the recovery duration.
         * @param duration The recovery duration
         * @return This builder instance
         */
        public Builder duration(final Duration duration) {
            this.duration = duration;
            return this;
        }
        
        /**
         * Sets the attempt count.
         * @param attemptCount The number of attempts
         * @return This builder instance
         */
        public Builder attemptCount(final int attemptCount) {
            this.attemptCount = attemptCount;
            return this;
        }
        
        /**
         * Sets the strategy used.
         * @param strategyUsed The recovery strategy name
         * @return This builder instance
         */
        public Builder strategyUsed(final String strategyUsed) {
            this.strategyUsed = strategyUsed;
            return this;
        }
        
        /**
         * Sets the recovery exception.
         * @param recoveryException The recovery exception
         * @return This builder instance
         */
        public Builder recoveryException(final Throwable recoveryException) {
            this.recoveryException = recoveryException;
            return this;
        }
        
        /**
         * Builds the RecoveryResult instance.
         * @return A new RecoveryResult instance
         */
        public RecoveryResult build() {
            return new RecoveryResult(
                successful,
                message,
                timestamp,
                duration,
                attemptCount,
                strategyUsed,
                recoveryException
            );
        }
    }
}