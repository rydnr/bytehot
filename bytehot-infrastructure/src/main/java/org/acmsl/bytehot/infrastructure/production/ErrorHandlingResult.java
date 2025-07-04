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
 * Filename: ErrorHandlingResult.java
 *
 * Author: Claude Code
 *
 * Class name: ErrorHandlingResult
 *
 * Responsibilities:
 *   - Represent the result of error handling operations
 *   - Track error classification and recovery outcomes
 *   - Provide comprehensive error handling context
 *
 * Collaborators:
 *   - ProductionErrorHandler: Creates and uses error handling results
 *   - ErrorClassification: Input classification information
 *   - RecoveryResult: Recovery attempt outcomes
 */
package org.acmsl.bytehot.infrastructure.production;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Represents the result of error handling operations.
 * @author Claude Code
 * @since 2025-07-04
 */
public class ErrorHandlingResult {
    
    /**
     * The outcome of error handling.
     */
    private final ErrorHandlingOutcome outcome;
    
    /**
     * The error classification used.
     */
    private final ErrorClassification classification;
    
    /**
     * The recovery result, if recovery was attempted.
     */
    private final RecoveryResult recoveryResult;
    
    /**
     * The incident ID if an incident was reported.
     */
    private final String incidentId;
    
    /**
     * The error handling timestamp.
     */
    private final Instant timestamp;
    
    /**
     * The total time spent handling the error.
     */
    private final Duration handlingDuration;
    
    /**
     * Additional context or message.
     */
    private final String message;
    
    /**
     * Creates a new ErrorHandlingResult.
     * @param outcome The error handling outcome
     * @param classification The error classification
     * @param recoveryResult The recovery result
     * @param incidentId The incident ID
     * @param timestamp The handling timestamp
     * @param handlingDuration The handling duration
     * @param message Additional context message
     */
    protected ErrorHandlingResult(final ErrorHandlingOutcome outcome,
                                final ErrorClassification classification,
                                final RecoveryResult recoveryResult,
                                final String incidentId,
                                final Instant timestamp,
                                final Duration handlingDuration,
                                final String message) {
        this.outcome = outcome;
        this.classification = classification;
        this.recoveryResult = recoveryResult;
        this.incidentId = incidentId;
        this.timestamp = timestamp;
        this.handlingDuration = handlingDuration;
        this.message = message;
    }
    
    /**
     * Creates a successful error handling result.
     * @param classification The error classification
     * @param recoveryResult The recovery result
     * @return A successful error handling result
     */
    public static ErrorHandlingResult success(final ErrorClassification classification,
                                            final RecoveryResult recoveryResult) {
        return new Builder()
            .outcome(ErrorHandlingOutcome.RECOVERED)
            .classification(classification)
            .recoveryResult(recoveryResult)
            .timestamp(Instant.now())
            .message("Error successfully handled and recovered")
            .build();
    }
    
    /**
     * Creates a failed error handling result.
     * @param classification The error classification
     * @param message The failure message
     * @return A failed error handling result
     */
    public static ErrorHandlingResult failed(final ErrorClassification classification,
                                           final String message) {
        return new Builder()
            .outcome(ErrorHandlingOutcome.FAILED)
            .classification(classification)
            .timestamp(Instant.now())
            .message(message)
            .build();
    }
    
    /**
     * Creates an error handling result with incident reporting.
     * @param classification The error classification
     * @param incidentId The incident ID
     * @return An error handling result with incident
     */
    public static ErrorHandlingResult withIncident(final ErrorClassification classification,
                                                 final String incidentId) {
        return new Builder()
            .outcome(ErrorHandlingOutcome.INCIDENT_REPORTED)
            .classification(classification)
            .incidentId(incidentId)
            .timestamp(Instant.now())
            .message("Error handled with incident reporting")
            .build();
    }
    
    /**
     * Creates a builder for constructing error handling results.
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Gets the error handling outcome.
     * @return The error handling outcome
     */
    public ErrorHandlingOutcome getOutcome() {
        return outcome;
    }
    
    /**
     * Gets the error classification.
     * @return The error classification
     */
    public ErrorClassification getClassification() {
        return classification;
    }
    
    /**
     * Gets the recovery result.
     * @return The recovery result, if any
     */
    public Optional<RecoveryResult> getRecoveryResult() {
        return Optional.ofNullable(recoveryResult);
    }
    
    /**
     * Gets the incident ID.
     * @return The incident ID, if any
     */
    public Optional<String> getIncidentId() {
        return Optional.ofNullable(incidentId);
    }
    
    /**
     * Gets the error handling timestamp.
     * @return The handling timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the handling duration.
     * @return The handling duration, if available
     */
    public Optional<Duration> getHandlingDuration() {
        return Optional.ofNullable(handlingDuration);
    }
    
    /**
     * Gets the result message.
     * @return The result message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Checks if the error handling was successful.
     * @return true if successful, false otherwise
     */
    public boolean isSuccessful() {
        return outcome == ErrorHandlingOutcome.RECOVERED || 
               outcome == ErrorHandlingOutcome.GRACEFULLY_DEGRADED;
    }
    
    /**
     * Builder for constructing ErrorHandlingResult instances.
     */
    public static class Builder {
        
        private ErrorHandlingOutcome outcome;
        private ErrorClassification classification;
        private RecoveryResult recoveryResult;
        private String incidentId;
        private Instant timestamp;
        private Duration handlingDuration;
        private String message;
        
        /**
         * Sets the error handling outcome.
         * @param outcome The error handling outcome
         * @return This builder instance
         */
        public Builder outcome(final ErrorHandlingOutcome outcome) {
            this.outcome = outcome;
            return this;
        }
        
        /**
         * Sets the error classification.
         * @param classification The error classification
         * @return This builder instance
         */
        public Builder classification(final ErrorClassification classification) {
            this.classification = classification;
            return this;
        }
        
        /**
         * Sets the recovery result.
         * @param recoveryResult The recovery result
         * @return This builder instance
         */
        public Builder recoveryResult(final RecoveryResult recoveryResult) {
            this.recoveryResult = recoveryResult;
            return this;
        }
        
        /**
         * Sets the incident ID.
         * @param incidentId The incident ID
         * @return This builder instance
         */
        public Builder incidentId(final String incidentId) {
            this.incidentId = incidentId;
            return this;
        }
        
        /**
         * Sets the handling timestamp.
         * @param timestamp The handling timestamp
         * @return This builder instance
         */
        public Builder timestamp(final Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        /**
         * Sets the handling duration.
         * @param handlingDuration The handling duration
         * @return This builder instance
         */
        public Builder handlingDuration(final Duration handlingDuration) {
            this.handlingDuration = handlingDuration;
            return this;
        }
        
        /**
         * Sets the result message.
         * @param message The result message
         * @return This builder instance
         */
        public Builder message(final String message) {
            this.message = message;
            return this;
        }
        
        /**
         * Builds the ErrorHandlingResult instance.
         * @return A new ErrorHandlingResult instance
         */
        public ErrorHandlingResult build() {
            return new ErrorHandlingResult(
                outcome,
                classification,
                recoveryResult,
                incidentId,
                timestamp,
                handlingDuration,
                message
            );
        }
    }
}